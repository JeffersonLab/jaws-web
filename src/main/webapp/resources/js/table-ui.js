let PAGE_SIZE = 100;

class TableUI extends EventTarget {
    constructor(panelElement, tableElement, options) {
        super();

        let me = this;

        me.panelElement = panelElement;
        me.tableElement = tableElement;

        me.options = options || {};

        me.rowDeselected = function() {
            $(me.panelElement + " .toolbar .no-selection-row-action").button("option", "disabled", true);
            $(me.panelElement + " .toolbar .selected-row-action").button("option", "disabled", false);
        }

        me.rowSelected = function() {
            $(me.panelElement + " .toolbar .no-selection-row-action").button("option", "disabled", false);
            $(me.panelElement + " .toolbar .selected-row-action").button("option", "disabled", true);
        }

        me.$nextButton = $(me.panelElement + " .next-button");
        me.$prevButton = $(me.panelElement + " .prev-button");

        me.options.rowSelected = me.rowDeselected;

        me.options.rowDeselected = me.rowSelected;

        me.filters = [];

        me.tabulator = new Tabulator(me.tableElement, me.options);

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
            let countCollection = table.orderBy('name');

            for(const filter of me.filters) {
                countCollection = countCollection.and(filter);
            }

            let selectCollection = countCollection.clone();

            let recordsPromise = selectCollection
                .limit(PAGE_SIZE)
                .toArray()
                .then((data) => {
                me.data = data;
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
                me.data = data;
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
                    me.data = data;
                    me.setData(data);

                    if(data.length === 0) {
                        // Uh oh
                    } else {
                        me.lastOffset = me.firstOffset - 1;
                        me.firstOffset = me.lastOffset - data.length + 1;
                        me.firstEntry = data[data.length - 1];
                        me.lastEntry = data[0];
                    }
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
            me.tabulator.setData(data);
        }
    }
}

export default TableUI