
class TableUI extends EventTarget {
    constructor(panelElement, tableElement, options) {
        super();

        this.panelElement = panelElement;
        this.tableElement = tableElement;

        this.options = options || {};

        this.options.rowSelected = () => {
            $(this.panelElement + " .toolbar .no-selection-row-action").button("option", "disabled", true);
            $(this.panelElement + " .toolbar .selected-row-action").button("option", "disabled", false);
        };

        this.options.rowDeselected = () => {
            $(this.panelElement + " .toolbar .no-selection-row-action").button("option", "disabled", false);
            $(this.panelElement + " .toolbar .selected-row-action").button("option", "disabled", true);
        };

        this.tabulator = new Tabulator(this.tableElement, this.options);

        this.updateCountLabel();

        // bind this on methods (not fool proof though since callers can also bind)
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