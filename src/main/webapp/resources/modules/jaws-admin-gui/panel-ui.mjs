import page from '../page-1.11.6/page.min.mjs';
import Editor from '../toast-ui-3.1.3/toastui-all.min.mjs';

let PAGE_SIZE = 100;

let entityMap = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
    '/': '&#x2F;',
    '`': '&#x60;',
    '=': '&#x3D;'
};

let escapeHtml = function(value) {
    return String(value).replace(/[&<>"'`=\/]/g, function (s) {
        return entityMap[s];
    });
}

let replaceEmpty = function(value, emptyValue) {
    return  (value === null || value === undefined ||  value === '') ? emptyValue : value;
}

let toStringDisplay = function(value, emptyValue) {
    return escapeHtml(replaceEmpty(value, emptyValue));
}

let toUnixTimestampDisplay = function(value, emptyValue) {
    let displayValue;

    if(value === null || value === undefined ||  value === '') {
        displayValue = emptyValue;
    } else {
        displayValue = (new Date(value)).toLocaleString();
    }

    return displayValue;
}

class PanelUI extends EventTarget {
    constructor(idPrefix, singularEntity, pluralEntity, store, path, types, markdownToHtml) {
        super();

        let me = this;

        me.idPrefix = idPrefix;
        me.singularEntity = singularEntity;
        me.pluralEntity = pluralEntity;
        me.store = store;
        me.path = path;
        me.types = types;
        me.markdownToHtml = markdownToHtml;

        me.sessionMessageCount = 0;
        me.visibleMessageCount = 0;

        me.panelElement = "#" + idPrefix + "-panel";
        me.tableElement = "#" + idPrefix + "-table";
        me.viewDialogElement = "#" + idPrefix + "-view-dialog";

        me.rowSelected = function() {
            $(me.panelElement + " .toolbar .no-selection-row-action").button("option", "disabled", true);
            $(me.panelElement + " .toolbar .selected-row-action").button("option", "disabled", false);
        }

        me.rowDeselected = function() {
            $(me.panelElement + " .toolbar .no-selection-row-action").button("option", "disabled", false);
            $(me.panelElement + " .toolbar .selected-row-action").button("option", "disabled", true);
        }

        $(me.tableElement).on("click", "tbody tr", function () {
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

            for(const widget of me.dialogmarkdownwidgets) {
                widget.destroy();
            }

            me.markdownwidgets = [];
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

        me.dialogmarkdownwidgets = [];

        me.toHtml = function(value, type, emptyValue) {
            let html;

            switch(type) {
                case "STRING":
                case "ENUM":
                case "MULTI_ENUM":
                case "BOOLEAN":
                case "NUMBER":
                    html = toStringDisplay(value, emptyValue);
                    break;
                case "UNIX_TIMESTAMP":
                    html = toUnixTimestampDisplay(value, emptyValue);
                    break;
                case "MARKDOWN":
                    me.markdownToHtml.setMarkdown(replaceEmpty(value, ' '));

                    html = me.markdownToHtml.getHTML();
                    break;
                default:
                    console.log('Unknown type: ', type);
            }

            return html;
        }

        me.showViewDialog = async function(key) {
            let data = await me.store.get(key);

            const map = new Map(Object.entries(data));

            let primaryKey = '';

            for (const [key, value] of map) {

                if(key == 'name') {
                    primaryKey = value;
                } else {
                    let selector = me.viewDialogElement + " ." + key + "-view",
                        type = me.types.get(key);

                    let html = me.toHtml(value, type, ' ');
                    $(selector).html(html);
                }
            }

            me.$viewDialog.dialog('option', 'title', me.singularEntity + ": " + primaryKey);

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

        me.filters = [];
        me.querystring = "";

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

        $(me.panelElement).on("click", ".refresh-button", function() {
            page(me.path);
        });

        me.renderProgress = function(highwaterReached) {

            $(me.panelElement + " .refresh-span").empty();

            if(me.sessionMessageCount == 0) {
                $(me.panelElement + " .progress divider").hide();
            } else {
                $(me.panelElement + " .progress divider").show();

                if(me.visibleMessageCount > 0) {
                    //console.log('render updates');
                    $(me.panelElement + " .progress").addClass("stale");

                    $(me.panelElement + " .session-message-count").text("New Updates: " + me.visibleMessageCount.toLocaleString());
                    if(highwaterReached) {
                        $(me.panelElement + " .refresh-span").append('<button type="button" class="refresh-button">Refresh</button>');
                    }
                } else {
                    //console.log('render all good');
                    $(me.panelElement + " .progress").removeClass("stale");

                    $(me.panelElement + " .session-message-count").text("Compacted: " + me.sessionMessageCount.toLocaleString());
                }
            }
        }

        me.renderTable = async function() {

            me.deselectRow();

            let countCollection = me.store.orderBy('name');

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
                $tbody = $(me.tableElement + " tbody"),
                columns = [...$th].map(th => $(th).text());

            $tbody.empty();

            for(const record of data) {
                let row = "<tr>";

                const map = new Map(Object.entries(record));

                for (const column of columns) {
                    let value = map.get(column),
                        type = me.types.get(column),
                        displayValue = me.toHtml(value, type, ' ');
                    row = row + "<td>" + displayValue + "</td>";
                }

                row = row + "</tr>";

                $tbody.append(row);
            }
        }

        me.updateSearchInput = function(querystring) {
            me.$searchTextElement.val(querystring.replaceAll('&', ',').replaceAll('=~', '~'));
            me.updateSearchFilter();
        }

        me.updateSearchFilter = function() {
            let filterText = me.$searchTextElement.val();

            let filterArray = filterText.split(",");

            me.filters = [];
            me.querystring = "";

            for (let filter of filterArray) {
                if(filter.indexOf('=') > -1) { // exact match equals search
                    let keyValue = filter.split("=");
                    me.filters.push(record => record[keyValue[0]] === keyValue[1]);
                    me.querystring = me.querystring + "&" + keyValue[0] + "=" + keyValue[1];
                } else if(filter.indexOf('~') > -1) { // case-insensitive contains search
                    let keyValue = filter.split("~");
                    me.filters.push(record => {
                        let haystack = record[keyValue[0]] || "";
                        let needle = keyValue[1] || "";
                        return haystack.toLowerCase().includes(needle.toLowerCase());
                    });
                    me.querystring = me.querystring + "&" + keyValue[0] + "=~" + keyValue[1];
                }
            }

            // Replace first & with ?
            if(filterArray.length > 0) {
                me.querystring = "?" + me.querystring.substring(1);
            }
        }

        me.search = function() {
            me.updateSearchFilter();

            me.renderTable();

            page(me.path + me.querystring);
        }
    }
}

export default PanelUI