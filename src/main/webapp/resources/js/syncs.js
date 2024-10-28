var jlab = jlab || {};
jlab.editableRowTable = jlab.editableRowTable || {};
jlab.editableRowTable.entity = 'Sync';
jlab.editableRowTable.dialog.width = 900;
jlab.editableRowTable.dialog.height = 600;
jlab.addRow = function (run) {
    var actionId = $("#row-action").val(),
        server = $("#row-server").val(),
        description = $("#row-description").val(),
        query = $("#row-query").val(),
        expression = $("#row-expression").val(),
        screencommand = $("#row-screencommand").val(),
        pv = $("#row-pv").val(),
        $button = $(".dialog-submit-button"),
        $runButton = $("#save-and-run-button"),
        reloading = false;

    if(expression != null) {
        expression = expression.replace(/\n/g, '&');
    }

    $button
        .height($button.height())
        .width($button.width())
        .empty().append('<div class="button-indicator"></div>');
    $runButton.attr("disabled", "disabled");
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: "/jaws/ajax/add-sync",
        type: "POST",
        data: {
            actionId: actionId,
            server: server,
            description: description,
            query: query,
            expression: expression,
            screencommand: screencommand,
            pv: pv
        },
        dataType: "json"
    });

    request.done(function (json) {
        if (json.stat === 'ok') {
            if(run) {
                window.location = 'syncs/' + json.id;
            } else {
                reloading = true;
                window.location.reload();
            }
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
            $button.empty().text("Save");
            $runButton.removeAttr("disabled");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
};
jlab.editRow = function (run) {
    var id = $(".editable-row-table tr.selected-row").attr("data-id"),
        actionId = $("#row-action").val(),
        server = $("#row-server").val(),
        description = $("#row-description").val(),
        query = $("#row-query").val(),
        expression = $("#row-expression").val(),
        screencommand = $("#row-screencommand").val(),
        pv = $("#row-pv").val(),
        $button = $(".dialog-submit-button"),
        $runButton = $("#save-and-run-button"),
        reloading = false;

    if(expression != null) {
        expression = expression.trim();
        expression = expression.replace(/\n/g, '&');
    }

    $button
        .height($button.height())
        .width($button.width())
        .empty().append('<div class="button-indicator"></div>');
    $runButton.attr("disabled", "disabled");
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: "/jaws/ajax/edit-sync",
        type: "POST",
        data: {
            id: id,
            actionId: actionId,
            server: server,
            description: description,
            query: query,
            expression: expression,
            screencommand: screencommand,
            pv: pv
        },
        dataType: "json"
    });

    request.done(function (json) {
        if (json.stat === 'ok') {
            if(run) {
                window.location = 'syncs/' + parseInt(id);
            } else {
                reloading = true;
                window.location.reload();
            }
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
            $runButton.removeAttr("disabled");
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

    $("#row-server").val($selectedRow.find("td:nth-child(3)").text());
    $("#row-description").val($selectedRow.find("td:nth-child(4)").text());
    $("#row-query").val($selectedRow.attr("data-query"));

    var expression = $selectedRow.attr("data-expression"),
        multilineExpression = "";

    if(expression != null) {
        multilineExpression = expression.replace(/&/g, '\n');
    }

    $("#row-expression").val(multilineExpression);
    $("#row-screencommand").val($selectedRow.attr("data-screencommand"));
    $("#row-pv").val($selectedRow.attr("data-pv"));
});
$(document).on("table-row-add", function () {
    jlab.addRow();
});
$(document).on("click", "#save-and-run-button", function () {
    if ($("#table-row-dialog").dialog("option", "title").startsWith("Add")) {
        jlab.addRow(true);
    } else {
        jlab.editRow(true);
    }
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
    $("#sync-rule-id").val('');
    $("#action-name").val('');
    $("#system-name").val('');
    return false;
});
$(function () {
    $("#table-row-dialog").dialog("option", "resizable", true);
    $("#table-row-dialog").dialog("option", "minWidth", 900);
    $("#table-row-dialog").dialog("option", "minHeight", 600);

    $("#rule-form-tabs").tabs();

    const urlParams = new URLSearchParams(window.location.search);
    const syncRuleId = urlParams.get('syncRuleId');
    const edit = urlParams.get('edit');

    if(edit === 'Y' && syncRuleId > -1) {
        urlParams.delete('edit');
        window.history.replaceState(null, null, 'syncs?' + urlParams.toString());
        $("#rule-table tbody tr:first-child").trigger('click');
        $("#open-edit-row-dialog-button").trigger('click');
    }
});