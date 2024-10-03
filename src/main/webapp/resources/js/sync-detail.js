var jlab = jlab || {};
jlab.addRow = function($tr) {
    var name = $tr.find("td:first-child").text(),
        actionId = $tr.attr("data-action-id"),
        locationCsv = $tr.attr("data-location-id-csv"),
        device = $tr.attr("data-device"),
        screenCommand = $tr.attr("data-screen-command"),
        managedBy = $tr.attr("data-managed-by"),
        maskedBy = $tr.attr("data-masked-by"),
        pv = $tr.attr("data-pv"),
        syncRuleId = $tr.attr("data-rule-id"),
        elementId = $tr.attr("data-element-id"),
        $button = $tr.find("button");

    let locationId = locationCsv.split(',');

    locationId = locationId.map(s => s.trim());

    $button
        .height($button.height())
        .width($button.width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: "/jaws/ajax/add-alarm",
        type: "POST",
        data: {
            name: name,
            actionId: actionId,
            locationId: locationId, /*renamed 'locationId[]' by jQuery*/
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
            $button.replaceWith("Success!");
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to add alarm; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        $button.empty().text("Add");
    });
};
$(document).on("click", "#add-table button", function() {
    jlab.addRow($(this).closest("tr"));
});