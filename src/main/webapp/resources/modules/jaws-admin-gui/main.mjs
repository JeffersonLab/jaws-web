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

        remote.addEventListener(me.batchEventName, (e) => {
            me.widget.sessionMessageCount = me.widget.sessionMessageCount + e.detail;
            if(me.highwaterReached && me.isVisible()) {
                me.widget.visibleMessageCount = me.widget.visibleMessageCount + e.detail;
                me.widget.renderProgress(me.highwaterReached);
            }
        });

        remote.addEventListener(me.highwaterEventName, () => {
            me.highwaterReached = true;
            if(me.isVisible()) {
                me.render();
            }
        });

        me.isVisible = function() {
            return $("#tabs ul li.ui-state-active").index() === me.order;
        }

        me.render = function() {
            if(me.highwaterReached) {
                me.widget.renderTable();
            }

            me.widget.visibleMessageCount = 0;
            me.widget.renderProgress(me.highwaterReached);
        }

        me.showSingleRecord = async function(ctx, next) {
            $("#tabs").tabs({ active: me.order });

            await me.widget.showViewDialog(ctx.params.name);
        }

        me.showAllRecords = async function(ctx, next) {
            console.log('showAllRecords', ctx, next);

            me.widget.updateSearchInput(ctx.querystring);

            await me.render();

            $("#tabs").tabs({ active: me.order });
        }

        me.tabActivated = function() {
            // On initial page load page triggers this automatically so skip if already at this route
            if(!page.current.startsWith(me.path)) {
                page(me.path + me.widget.querystring);
            } else {
                console.log('skipping since already at route');
            }
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

            for(let controller of controllers) {

                console.log('looking for: ', i.newTab.context.innerText);

                if(controller.pluralEntityName === i.newTab.context.innerText) {
                    console.log('found: ', controller);
                    controller.tabActivated();
                    break;
                }
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
