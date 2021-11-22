import db from "./db.js";

let PAGE_SIZE = 100;

class TableUI extends EventTarget {
    constructor(panelElement, tableElement, options) {
        super();

        this.panelElement = panelElement;
        this.tableElement = tableElement;

        this.options = options || {};

        let that = this;

        this.rowDeselected = function() {
            $(that.panelElement + " .toolbar .no-selection-row-action").button("option", "disabled", true);
            $(that.panelElement + " .toolbar .selected-row-action").button("option", "disabled", false);
        }

        this.rowSelected = function() {
            $(that.panelElement + " .toolbar .no-selection-row-action").button("option", "disabled", false);
            $(that.panelElement + " .toolbar .selected-row-action").button("option", "disabled", true);
        }

        this.options.rowSelected = this.rowDeselected;

        this.options.rowDeselected = this.rowSelected;

        this.tabulator = new Tabulator(this.tableElement, this.options);

        // bind this on methods
        const methods = Object.getOwnPropertyNames(Object.getPrototypeOf(this));

        methods
            .filter(method => (method !== 'constructor'))
            .forEach((method) => { this[method] = this[method].bind(this);});
    }

    async refresh(table) {
        let d, c;
        let recordsPromise = table.orderBy('name').limit(PAGE_SIZE).toArray().then((data) => {
            d = data;
            this.setData(data)
        });

        let countPromise = table.count().then((count) => {
            c = count;
        });

        return Promise.all([recordsPromise, countPromise]).then(() => this.updateCountLabel(1, Math.min(PAGE_SIZE, d.length),  c));
    }

    updateCountLabel(offset, max, count) {
        $(this.panelElement + " .record-count").text(offset.toLocaleString() + ' - ' + max.toLocaleString() + ' of ' + count.toLocaleString());
    }

    setData(data) {
        this.tabulator.setData(data);
    }
}

export default TableUI