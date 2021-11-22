
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

        this.updateCountLabel();

        // bind this on methods
        const methods = Object.getOwnPropertyNames(Object.getPrototypeOf(this));

        methods
            .filter(method => (method !== 'constructor'))
            .forEach((method) => { this[method] = this[method].bind(this);});
    }

    updateCountLabel() {
        let count = this.tabulator.getDataCount("active");

        let sorters = this.tabulator.getSorters();
        this.tabulator.setSort(sorters);

        $(this.panelElement + " .record-count").text(count.toLocaleString());
    }

    setData(data) {
        this.tabulator.setData(data);
        this.updateCountLabel();
    }
}

export default TableUI