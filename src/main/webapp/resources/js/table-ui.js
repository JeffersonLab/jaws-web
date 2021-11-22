import db from "./db.js";

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

        me.options.rowSelected = me.rowDeselected;

        me.options.rowDeselected = me.rowSelected;

        me.tabulator = new Tabulator(me.tableElement, me.options);

        me.refresh = async function(table) {
            let d, c;
            let recordsPromise = table.orderBy('name').limit(PAGE_SIZE).toArray().then((data) => {
                d = data;
                me.setData(data);
            });

            let countPromise = table.count().then((count) => {
                c = count;
            });

            return Promise.all([recordsPromise, countPromise]).then(() => me.updateCountLabel(1, Math.min(PAGE_SIZE, d.length),  c));
        }

        me.updateCountLabel = function(offset, max, count) {
            $(me.panelElement + " .record-count").text(offset.toLocaleString() + ' - ' + max.toLocaleString() + ' of ' + count.toLocaleString());
        }

        me.setData = function(data) {
            me.tabulator.setData(data);
        }
    }
}

export default TableUI