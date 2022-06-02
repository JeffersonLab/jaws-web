import page from '../page-1.11.6/page.min.mjs';
import ui from "./ui.mjs";
import db from "./db.mjs";

let PAGE_SIZE = 100;

class PanelUI extends EventTarget {
    constructor(id, store, path) {
        super();

        let me = this;

        me.panelElement = id + "-panel";
        me.tableElement = id + "-table";
        me.viewDialogElement = id + "-view-dialog";
        me.store = store;
        me.path = path;

        me.rowSelected = function() {
            $(me.panelElement + " .toolbar .no-selection-row-action").button("option", "disabled", true);
            $(me.panelElement + " .toolbar .selected-row-action").button("option", "disabled", false);
        }

        me.rowDeselected = function() {
            $(me.panelElement + " .toolbar .no-selection-row-action").button("option", "disabled", false);
            $(me.panelElement + " .toolbar .selected-row-action").button("option", "disabled", true);
        }

        $(me.tableElement).on("click", ".inner-table tbody tr", function () {
            let $previouslySelected = $(me.tableElement + " .selected-row"),
                deselect = $(this).hasClass("selected-row");

            $previouslySelected.removeClass("selected-row");

            if(deselect) {
                me.rowDeselected();
            } else {
                $(this).addClass("selected-row");
                me.rowSelected();
            }
        });

        $(me.panelElement).on("click", ".view-button", function() {
            let key = $(me.tableElement + " .selected-row td:first").text();

            page(me.path + "/" + key);
        });

        let closeDialog = function() {
            me.$viewDialog.dialog("close");
            page(me.path);
            me.deselectRow();
        }

        me.$viewDialog = $(me.viewDialogElement).dialog({
            autoOpen: false,
            height: 550,
            width: 750,
            modal: true,
            close: closeDialog,
            buttons: {
                OK: closeDialog
            }
        });


        me.showViewDialog = async function(key) {
            let data = await me.store.get(key);

            const map = new Map(Object.entries(data));

            for (const [key, value] of map) {
                $(me.viewDialogElement + " ." + key + "-view").text(value);
            }

            me.$viewDialog.dialog("open");
        }

        me.$nextButton = $(me.panelElement + " .next-button");
        me.$prevButton = $(me.panelElement + " .prev-button");

        $(me.$nextButton).on("click", function() {
            me.next(store);
        });

        $(me.$prevButton).on("click", function() {
            me.previous(store);
        });

        me.$searchTextElement = $(me.panelElement + " .search-input");
        me.$searchForm = $(me.panelElement + " .search-form");
        me.$searchButton = $(me.panelElement + " .search-button");

        $(me.$searchButton).on("click", function() {
            me.search();
        });

        $(me.$searchForm).on("submit", function (event) {
            event.preventDefault();
            me.search();
        });

        $(document).on("click", "#search-class-button", function () {
            ui.classSearch();
        });

        me.filters = [];

        me.deselectRow = function() {
            $(me.tableElement + " .selected-row").removeClass("selected-row");
            me.rowDeselected();
        }

        me.updatePaginationToolbar = function() {
            me.updateCountLabel(me.firstOffset, me.lastOffset,  me.count);

            if(me.firstOffset > 1) {
                // Enable prev button
                me.$prevButton.button("option", "disabled", false);
            } else {
                // Disable prev button
                me.$prevButton.button("option", "disabled", true);
            }

            if (me.lastOffset < me.count) {
                // Enable next button
                me.$nextButton.button("option", "disabled", false);
            } else {
                // Disable next button
                me.$nextButton.button("option", "disabled", true);
            }
        }

        me.refresh = async function(table) {
            me.deselectRow();

            let countCollection = table.orderBy('name');

            for(const filter of me.filters) {
                countCollection = countCollection.and(filter);
            }

            let selectCollection = countCollection.clone();

            let recordsPromise = selectCollection
                .limit(PAGE_SIZE)
                .toArray()
                .then((data) => {
                me.setData(data);

                if(data.length == 0) {
                    // no results
                    me.firstOffset = 0;
                    me.lastOffset = 0;
                    me.firstEntry = null;
                    me.lastEntry = null;
                } else {
                    me.firstOffset = 1;
                    me.lastOffset = data.length;
                    me.firstEntry = data[0];
                    me.lastEntry = data[data.length-1];
                }
            });

            let countPromise = countCollection.count().then((count) => {
                me.count = count;
            });

            return Promise.all([recordsPromise, countPromise]).then(() => {
                me.updatePaginationToolbar();
            });
        }

        me.next = async function(table) {
            me.deselectRow();

            let selectCollection = table.where('name')
                .above(me.lastEntry.name);

            let countCollection = table.orderBy('name');

            for(const filter of me.filters) {
                selectCollection = selectCollection.and(filter);
                countCollection = countCollection.and(filter);
            }

            let recordsPromise = selectCollection
                .limit(PAGE_SIZE)
                .toArray()
                .then((data) => {
                me.setData(data);

                if(data.length === 0) {
                    // Uh oh
                } else {
                    me.firstOffset = me.lastOffset + 1;
                    me.lastOffset = me.firstOffset + data.length - 1;
                    me.firstEntry = data[0];
                    me.lastEntry = data[data.length - 1];
                }
            });

            let countPromise = countCollection.count().then((count) => {
                me.count = count;
            });

            return Promise.all([recordsPromise, countPromise]).then(() => {
                me.updatePaginationToolbar();
            });
        }

        me.previous = async function(table) {
            me.deselectRow();

            let selectCollection = table.where('name')
                .below(me.firstEntry.name);

            let countCollection = table.orderBy('name');

            for(const filter of me.filters) {
                selectCollection = selectCollection.and(filter);
                countCollection = countCollection.and(filter);
            }

            let recordsPromise = selectCollection
                .reverse()
                .limit(PAGE_SIZE)
                .toArray()
                .then((data) => {
                    if(data.length === 0) {
                        // Uh oh
                    } else {
                        me.lastOffset = me.firstOffset - 1;
                        me.firstOffset = me.lastOffset - data.length + 1;
                        me.firstEntry = data[data.length - 1];
                        me.lastEntry = data[0];
                    }

                    data.reverse();
                    me.setData(data);
                });

            let countPromise = countCollection.count().then((count) => {
                me.count = count;
            });

            return Promise.all([recordsPromise, countPromise]).then(() => {
                me.updatePaginationToolbar();
            });
        }

        me.updateCountLabel = function(offset, max, count) {
            if(count === 0) {
                $(me.panelElement + " .record-count").text("0");
            } else {
                $(me.panelElement + " .record-count").text(offset.toLocaleString() + ' - ' + max.toLocaleString() + ' of ' + count.toLocaleString());
            }
        }

        me.setData = function(data) {
            me.data = data;
            me.updateTableData(data);
        }

        me.updateTableData = function(data) {
            let $th = $(me.tableElement + " thead th"),
                $tbody = $(me.tableElement + " .inner-table tbody"),
                columns = [...$th].map(th => $(th).text());

            columns.pop(); // expand icon column

            $tbody.empty();

            for(const record of data) {
                let row = "<tr>";

                const map = new Map(Object.entries(record));

                for (const column of columns) {
                    row = row + "<td>" + map.get(column) + "</td>";
                }

                row = row + "</tr>";

                $tbody.append(row);
            }
        }

        me.search = function() {
            let filterText = me.$searchTextElement.val();

            let filterArray = filterText.split(",");

            me.filters = [];

            for (let filter of filterArray) {
                if(filter.indexOf('=') > -1) { // exact match equals search
                    let keyValue = filter.split("=");
                    me.filters.push(record => record[keyValue[0]] === keyValue[1]);
                } else if(filter.indexOf('~') > -1) { // case-insensitive contains search
                    let keyValue = filter.split("~");
                    me.filters.push(record => {
                        let haystack = record[keyValue[0]] || "";
                        let needle = keyValue[1] || "";
                        return haystack.toLowerCase().includes(needle.toLowerCase());
                    });
                }
            }

            me.refresh(me.store);
        }
    }
}

export default PanelUI