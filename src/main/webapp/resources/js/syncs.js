var jlab = jlab || {};
jlab.editableRowTable = jlab.editableRowTable || {};
jlab.editableRowTable.entity = 'Sync';
jlab.editableRowTable.dialog.width = 800;
jlab.editableRowTable.dialog.height = 400;
jlab.addRow = function () {
    var actionId = $("#row-action").val(),
        deployment = $("#row-deployment").val(),
        query = $("#row-query").val(),
        screencommand = $("#row-screencommand").val(),
        pv = $("#row-pv").val(),
        reloading = false;

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: "/jaws/ajax/add-sync",
        type: "POST",
        data: {
            actionId: actionId,
            deployment: deployment,
            query: query,
            screencommand: screencommand,
            pv: pv
        },
        dataType: "json"
    });

    request.done(function (json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to add sync; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function () {
        if (!reloading) {
            $(".dialog-submit-button").empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
};
jlab.editRow = function () {
    var id = $(".editable-row-table tr.selected-row").attr("data-id"),
        actionId = $("#row-action").val(),
        deployment = $("#row-deployment").val(),
        query = $("#row-query").val(),
        screencommand = $("#row-screencommand").val(),
        pv = $("#row-pv").val(),
        reloading = false;

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: "/jaws/ajax/edit-sync",
        type: "POST",
        data: {
            id: id,
            actionId: actionId,
            deployment: deployment,
            query: query,
            screencommand: screencommand,
            pv: pv
        },
        dataType: "json"
    });

    request.done(function (json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to edit action; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function () {
        if (!reloading) {
            $(".dialog-submit-button").empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
};
jlab.removeRow = function () {
    var name = $(".editable-row-table tr.selected-row td:first-child").text(),
        id = $(".editable-row-table tr.selected-row").attr("data-id"),
        reloading = false;

    $("#remove-row-button")
        .height($("#remove-row-button").height())
        .width($("#remove-row-button").width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: "/jaws/ajax/remove-sync",
        type: "POST",
        data: {
            id: id
        },
        dataType: "json"
    });

    request.done(function (json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function (xhr, textStatus) {
        window.console && console.log('Unable to remove action; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Remove Server unavailable or unresponsive');
    });

    request.always(function () {
        if (!reloading) {
            $("#remove-row-button").empty().text("Remove");
        }
    });
};
$(document).on("dialogclose", "#table-row-dialog", function () {
    $("#row-form")[0].reset();
});
$(document).on("click", "#open-edit-row-dialog-button", function () {
    var $selectedRow = $(".editable-row-table tr.selected-row");
    $("#row-action").val($selectedRow.attr("data-action-id"));

    $("#row-deployment").val($selectedRow.find("td:nth-child(3)").text());
    $("#row-query").val($selectedRow.find("td:nth-child(4)").text());
    $("#row-screencommand").val($selectedRow.attr("data-screencommand"));
    $("#row-pv").val($selectedRow.attr("data-pv"));
});
$(document).on("table-row-add", function () {
    jlab.addRow();
});
$(document).on("table-row-edit", function () {
    jlab.editRow();
});
$(document).on("click", "#remove-row-button", function () {
    var name = $(".editable-row-table tr.selected-row td:first-child").text().trim();
    if (confirm('Are you sure you want to remove ' + name + '?')) {
        jlab.removeRow();
    }
});
$(document).on("click", ".default-clear-panel", function () {
    $("#sync-id").val('');
    $("#action-name").val('');
    return false;
});
$(function () {
    $("#table-row-dialog").dialog("option", "resizable", true);
    $("#table-row-dialog").dialog("option", "minWidth", 800);
    $("#table-row-dialog").dialog("option", "minHeight", 500);
});