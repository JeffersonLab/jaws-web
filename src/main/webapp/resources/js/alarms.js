var jlab = jlab || {};
jlab.editableRowTable = jlab.editableRowTable || {};
jlab.editableRowTable.entity = 'Alarm';
jlab.editableRowTable.dialog.width = 600;
jlab.editableRowTable.dialog.height = 500;
jlab.addRow = function() {
    var name = $("#row-name").val(),
        actionId = $("#row-action").val(),
        locationData = $("#row-location").select2('data');
        alias = $("#row-alias").val(),
        device = $("#row-device").val(),
        screenCommand = $("#row-screen-command").val(),
        managedBy = $("#row-managed-by").val(),
        maskedBy = $("#row-masked-by").val(),
        pv = $("#row-pv").val(),
        reloading = false;

    let locationId = locationData.map(a => a.id);

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: "/jaws/ajax/add-alarm",
        type: "POST",
        data: {
            name: name,
            actionId: actionId,
            locationId: locationId, /*renamed 'locationId[]' by jQuery*/
            alias: alias,
            device: device,
            screenCommand: screenCommand,
            managedBy: managedBy,
            maskedBy: maskedBy,
            pv: pv
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to add alarm; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $(".dialog-submit-button").empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
};
jlab.editRow = function() {
    var name = $("#row-name").val(),
        actionId = $("#row-action").val(),
        locationData = $("#row-location").select2('data');
        alias = $("#row-alias").val(),
        device = $("#row-device").val(),
        screenCommand = $("#row-screen-command").val(),
        managedBy = $("#row-managed-by").val(),
        maskedBy = $("#row-masked-by").val(),
        pv = $("#row-pv").val(),
        syncRuleId = $("#row-sync-rule-id").val(),
        elementId = $("#row-sync-element-id").val(),
        alarmId = $(".editable-row-table tr.selected-row").attr("data-id"),
        reloading = false;

    let locationId = locationData.map(a => a.id);

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: "/jaws/ajax/edit-alarm",
        type: "POST",
        data: {
            alarmId: alarmId,
            name: name,
            actionId: actionId,
            locationId: locationId, /*renamed 'locationId[]' by jQuery*/
            alias: alias,
            device: device,
            screenCommand: screenCommand,
            managedBy: managedBy,
            maskedBy: maskedBy,
            pv: pv,
            syncRuleId: syncRuleId,
            elementId: elementId
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to edit alarm; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $(".dialog-submit-button").empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
};
jlab.removeRow = function() {
    var name = $(".editable-row-table tr.selected-row td:first-child").text(),
        id = $(".editable-row-table tr.selected-row").attr("data-id"),
        reloading = false;

    $("#remove-row-button")
        .height($("#remove-row-button").height())
        .width($("#remove-row-button").width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: "/jaws/ajax/remove-alarm",
        type: "POST",
        data: {
            id: id
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to remove alarm; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Remove Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $("#remove-row-button").empty().text("Remove");
        }
    });
};
$(document).on("dialogclose", "#table-row-dialog", function() {
    $("#row-form")[0].reset();
});
$(document).on("click", "#open-edit-row-dialog-button", function() {
    var $selectedRow = $(".editable-row-table tr.selected-row");
    $("#row-name").val($selectedRow.find("td:first-child a").text());
    $("#row-action").val($selectedRow.attr("data-action-id"));
    $("#row-alias").val($selectedRow.attr("data-alias"));
    $("#row-device").val($selectedRow.attr("data-device"));
    $("#row-screen-command").val($selectedRow.attr("data-screen-command"));
    $("#row-managed-by").val($selectedRow.attr("data-managed-by"));
    $("#row-masked-by").val($selectedRow.attr("data-masked-by"));
    $("#row-pv").val($selectedRow.attr("data-pv"));
    $("#row-sync-rule-id").val($selectedRow.attr("data-sync-rule-id"));
    $("#row-sync-element-id").val($selectedRow.attr("data-sync-element-id"));

    let locationIdCsv = $selectedRow.attr("data-location-id-csv"),
        locationIdArray = locationIdCsv.split(/[ ,]+/);
    $('#row-location').val(locationIdArray).trigger('change');
});
$(document).on("table-row-add", function() {
    jlab.addRow();
});
$(document).on("table-row-edit", function() {
    jlab.editRow();
});
$(document).on("click", "#remove-row-button", function() {
    var name = $(".editable-row-table tr.selected-row td:first-child").text().trim();
    if (confirm('Are you sure you want to remove ' + name + '?')) {
        jlab.removeRow();
    }
});
$(document).on("click", ".default-clear-panel", function () {
    $("#location-select").val(null).trigger('change');
    $("#priority-select").val('');
    $("#team-select").val('');
    $("#synced-select").val('');
    $("#pv").val('');
    $("#alarm-name").val('');
    $("#action-name").val('');
    $("#system-name").val('');
    return false;
});
function formatLocation(location) {
    return location.text.trim();
}
$(function () {
    $("#location-select").select2({
        width: 390,
        templateSelection: formatLocation
    });
    $("#row-location").select2({
        width: 290,
        templateSelection: formatLocation
    });

    const urlParams = new URLSearchParams(window.location.search);
    const alarmName = urlParams.get('alarmName');
    const edit = urlParams.get('edit');

    if(edit === 'Y' && alarmName !== undefined) {
        urlParams.delete('edit');
        window.history.replaceState(null, null, 'alarms?' + urlParams.toString());
        $(".inner-table tbody tr:first-child").trigger('click');
        $("#open-edit-row-dialog-button").trigger('click');
    }
});