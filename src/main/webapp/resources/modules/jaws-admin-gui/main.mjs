import remote from './remote.mjs';
import db from './db.mjs';
import PanelUI from './panel-ui.mjs';
import page from '../page-1.11.6/page.min.mjs';
import jawsTypes from './jaws-types.mjs';
import Editor from '../toast-ui-3.1.3/toastui-all.min.mjs';

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

        me.markdownToHTMLEditor = Editor.factory({
            viewer: false, /* No toHTML() method unless full editor! */
            usageStatistics: false,
            autofocus: false,
            el: document.querySelector("#markdown-to-html")
        });

        me.widget = new PanelUI(idPrefix, singularEntityName, pluralEntityName, store, path, jawsTypes[me.idPrefix], me.markdownToHTMLEditor);

        me.highwaterEventName = batchEventName + "-highwatermark";
        me.highwaterReached = false;

        remote.addEventListener(me.highwaterEventName, async () => {
            me.highwaterReached = true;
            if(me.isVisible()) {
                await me.render();
            }
        });

        remote.addEventListener(me.batchEventName, async (e) => {
            me.widget.sessionMessageCount = e.detail;
            if(me.isVisible()) {
                await me.widget.renderProgress(true);
            }
        });

        me.isVisible = function() {
            return $("#tabs ul li.ui-state-active").index() === me.order;
        }

        me.render = async function() {
            if(me.highwaterReached) {
                await me.widget.renderTable();
            }

            await me.widget.renderProgress(false);
        }

        me.showSingleRecord = async function(ctx, next) {
            $("#tabs").tabs({ active: me.order });

            await me.widget.showViewDialog(ctx.params.name);
        }

        me.showAllRecords = async function(ctx, next) {
            await me.render();
            $("#tabs").tabs({ active: me.order });
        }

        page(me.path, me.showAllRecords);
        page(me.path + '/:name', me.showSingleRecord);
    }
}

let controllers = [new PanelController(0, 'alarm', 'alarms', 'Alarm', 'Alarms', db.alarms, '/alarms'),
                   new PanelController(1, 'notification', 'notifications', 'Notification', 'Notifications', db.notifications, '/notifications'),
                   new PanelController(2, 'registration', 'registrations', 'Registration', 'Registrations', db.registrations, '/registrations'),
                   new PanelController(3, 'activation', 'activations', 'Activation', 'Activations', db.activations, '/activations'),
                   new PanelController(4, 'override', 'overrides', 'Override', 'Overrides', db.overrides, '/overrides'),
                   new PanelController(5, 'class', 'classes', 'Class', 'Classes', db.classes, '/classes'),
                   new PanelController(6, 'instance', 'instances', 'Instance', 'Instances', db.instances, '/instances'),
                   new PanelController(7, 'location', 'locations', 'Location', 'Locations', db.locations, '/locations'),
                   new PanelController(8, 'category', 'categories', 'Category', 'Categories', db.categories, '/categories')];


page.base(contextPath);

page('/', function() {
    page('/alarms');
});

$(function () {
    $(".toolbar button").button();

    page();

    $("#tabs").tabs({
        activate: function (event, i) {
            i.newPanel.css("display", "flex");

            switch (i.newTab.context.innerText) {
                case 'Alarms':
                    page('/alarms');
                    break;
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
                case 'Notifications':
                    page('/notifications');
                    break;
                case 'Overrides':
                    page('/overrides');
                    break;
                case 'Registrations':
                    page('/registrations');
                    break;
                default:
                    console.log('Unknown tab activation: ', event, i);
            }
        }
    }).show();

    $("#reset-button").on("click", function() {
        remote.clear();
    });

    remote.addEventListener("cleared", async () => {
        location.reload();
    });
});
