import remote from './remote.mjs';
import db from './db.mjs';
import PanelUI from './panel-ui.mjs';
import page from '../page-1.11.6/page.min.mjs';

const meta = document.querySelector('meta');
const contextPath = meta && meta.dataset.contextPath || '';

class PanelController {
    constructor(order, batchEventName, idPrefix, singularEntityName, pluralEntityName, store, path) {
        let me = this;

        me.order = order;
        me.batchEventName = batchEventName;
        me.idPrefix = idPrefix;
        me.singularEntityName = singularEntityName;
        me.pluralEntityName = pluralEntityName;
        me.store = store;
        me.path = path;

        me.widget = new PanelUI(idPrefix, singularEntityName, pluralEntityName, store, path);

        me.highwaterEventName = batchEventName + "-highwatermark";
        me.highwaterReached = false;

        remote.addEventListener(me.highwaterEventName, async () => {
            await me.widget.refresh();
            me.highwaterReached = true;
        });

        remote.addEventListener(me.batchEventName, async () => {
            if(me.highwaterReached) {
                await me.widget.refresh();
            }
        });

        me.showSingleRecord = async function(ctx, next) {
            $("#tabs").tabs({ active: me.order });

            await me.widget.showViewDialog(ctx.params.name);
        }

        me.showAllRecords = async function(ctx, next) {
            $("#tabs").tabs({ active: me.order });
        }

        page(me.path, me.showAllRecords);
        page(me.path + '/:name', me.showSingleRecord);
    }
}

let controllers = [new PanelController(0, 'registration', '#registrations', 'Registration', 'Registrations', db.registrations, '/registrations'),
                   new PanelController(1, 'activation', '#activations', 'Activation', 'Activations', db.activations, '/activations'),
                   new PanelController(2, 'class', '#classes', 'Class', 'Classes', db.classes, '/classes'),
                   new PanelController(3, 'instance', '#instances', 'Instance', 'Instances', db.instances, '/instances'),
                   new PanelController(4, 'location', '#locations', 'Location', 'Locations', db.locations, '/locations'),
                   new PanelController(5, 'category', '#categories', 'Category', 'Categories', db.categories, '/categories')];


page.base(contextPath + '/view');

page('/', function() {
    console.log('tabs init');
    page('/registrations');
});

page();

$(function () {
    $(".toolbar button").button();

    $("#tabs").tabs({
        activate: function (event, i) {
            i.newPanel.css("display", "flex");

            switch (i.newTab.context.innerText) {
                case 'Activations':
                    page('/activations');
                    break;
                case 'Categories':
                    page('/categories');
                    break;
                case 'Classes':
                    page('/classes');
                    break;
                case 'Instances':
                    page('/instances');
                    break;
                case 'Locations':
                    page('/locations');
                    break;
                case 'Registrations':
                    page('/registrations');
                    break;
                default:
                    console.log('Unknown tab activation: ', event, i);
            }
        }
    }).show();
});
