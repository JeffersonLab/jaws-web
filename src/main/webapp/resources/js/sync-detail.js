var jlab = jlab || {};
jlab.addRow = function($tr, batch) {
    var name = $tr.attr("data-name"),
        actionId = $tr.attr("data-action-id"),
        locationCsv = $tr.attr("data-location-id-csv"),
        alias = $tr.attr("data-alias"),
        device = $tr.attr("data-device"),
        screenCommand = $tr.attr("data-screen-command"),
        managedBy = $tr.attr("data-managed-by"),
        maskedBy = $tr.attr("data-masked-by"),
        pv = $tr.attr("data-pv"),
        syncRuleId = $tr.attr("data-rule-id"),
        elementId = $tr.attr("data-element-id"),
        $button = $tr.find("button.add");

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
            $button.replaceWith("Success!");

            if(batch) {
                var $tr = jlab.addTr.pop();
                if($tr !== undefined) {
                    jlab.addRow($tr, true);
                } else {
                    if(jlab.removeTr.length === 0 && jlab.updateTr.length === 0 && jlab.linkButton.length === 0) {
                        $("#apply-all-button").replaceWith("Done!");
                    }
                }
            }
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
jlab.removeRow = function($tr, batch) {
    var name = $tr.find("td:nth-child(2)").text(),
        id = $tr.attr("data-id"),
        $button = $tr.find("button");

    $button
        .height($button.height())
        .width($button.width())
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
            $button.replaceWith("Success!");

            if(batch) {
                var $tr = jlab.removeTr.pop();
                if($tr !== undefined) {
                    jlab.removeRow($tr, true);
                } else {
                    if(jlab.addTr.length === 0 && jlab.updateTr.length === 0 && jlab.linkButton.length === 0) {
                        $("#apply-all-button").replaceWith("Done!");
                    }
                }
            }
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to remove alarm; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Remove Server unavailable or unresponsive');
    });

    request.always(function() {
        $button.empty().text("Remove");
    });
};
jlab.linkRow = function($tr, alarmId, batch) {
    var name = $tr.attr("data-name"),
        actionId = $tr.attr("data-action-id"),
        locationCsv = $tr.attr("data-location-id-csv"),
        alias = $tr.attr("data-alias"),
        device = $tr.attr("data-device"),
        screenCommand = $tr.attr("data-screen-command"),
        managedBy = $tr.attr("data-managed-by"),
        maskedBy = $tr.attr("data-masked-by"),
        pv = $tr.attr("data-pv"),
        syncRuleId = $tr.attr("data-rule-id"),
        elementId = $tr.attr("data-element-id"),
        $button = $tr.find("button.link");

    let locationId = locationCsv.split(',');

    locationId = locationId.map(s => s.trim());

    $button
        .height($button.height())
        .width($button.width())
        .empty().append('<div class="button-indicator"></div>');

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
            $button.replaceWith("Success!");

            if(batch) {
                var $b = jlab.linkButton.pop();
                if($b !== undefined) {
                    jlab.linkRow($b.closest("tr"), $b.attr("data-alarm-id"), true);
                } else {
                    if(jlab.addTr.length === 0 && jlab.removeTr.length === 0 && jlab.updateTr.length === 0) {
                        $("#apply-all-button").replaceWith("Done!");
                    }
                }
            }
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to link alarm; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        $button.empty().text("Link");
    });
};
jlab.updateRow = function($tr, batch) {
    var alarmId = $tr.attr("data-id"),
        name = $tr.attr("data-name"),
        actionId = $tr.attr("data-action-id"),
        locationCsv = $tr.attr("data-location-id-csv"),
        alias = $tr.attr("data-alias"),
        device = $tr.attr("data-device"),
        screenCommand = $tr.attr("data-screen-command"),
        managedBy = $tr.attr("data-managed-by"),
        maskedBy = $tr.attr("data-masked-by"),
        pv = $tr.attr("data-pv"),
        syncRuleId = $tr.attr("data-rule-id"),
        elementId = $tr.attr("data-element-id"),
        $button = $tr.find("button.update");

    let locationId = locationCsv.split(',');

    locationId = locationId.map(s => s.trim());

    $button
        .height($button.height())
        .width($button.width())
        .empty().append('<div class="button-indicator"></div>');

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
            $button.replaceWith("Success!");

            if(batch) {
                var $tr = jlab.updateTr.pop();
                if($tr !== undefined) {
                    jlab.updateRow($tr, true);
                } else {
                    if(jlab.addTr.length === 0 && jlab.removeTr.length === 0 && jlab.linkButton.length === 0) {
                        $("#apply-all-button").replaceWith("Done!");
                    }
                }
            }
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to update alarm; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        $button.empty().text("Update");
    });
};
$(document).on("click", "#apply-all-button", function() {
    var $table = $(this).closest("table"),
        $updateButtons = $table.find("button.update"),
        $removeButtons = $table.find("button.remove"),
        $addButtons = $table.find("button.add"),
        $linkButtons = $table.find("button.autolink");

    if($updateButtons.length > 0 || $removeButtons.length > 0 || $addButtons.length > 0 || $linkButtons.length > 0) {
        var questionArray = [];

        if($updateButtons.length > 0) {
            questionArray.push('Update ' + $updateButtons.length);
        }

        if($removeButtons.length > 0) {
            questionArray.push('Remove ' + $removeButtons.length);
        }

        if($addButtons.length > 0) {
            questionArray.push('Add ' + $addButtons.length);
        }

        if($linkButtons.length > 0) {
            questionArray.push('Link ' + $linkButtons.length);
        }

        var question = 'Are you sure you want to ' + questionArray.join(', ');

        if(confirm(question)) {

            var $button = $("#apply-all-button");

            $button
                .height($button.height())
                .width($button.width())
                .empty().append('<div class="button-indicator"></div>');

            $button.attr("disabled", "disabled");

            jlab.updateTr = [];
            $updateButtons.each(function() {
                jlab.updateTr.push($(this).closest("tr"));
            });
            jlab.removeTr = [];
            $removeButtons.each(function() {
                jlab.removeTr.push($(this).closest("tr"));
            });
            jlab.addTr = [];
            $addButtons.each(function() {
                jlab.addTr.push($(this).closest("tr"));
            });
            jlab.linkButton = [];
            $linkButtons.each(function() {
                jlab.linkButton.push($(this));
            });

            if(jlab.updateTr.length > 0) {
                jlab.updateTr.reverse();

                jlab.updateRow(jlab.updateTr.pop(), true);
            }

            if(jlab.removeTr.length > 0) {
                jlab.removeTr.reverse();

                jlab.removeRow(jlab.removeTr.pop(), true);
            }

            if(jlab.addTr.length > 0) {
                jlab.addTr.reverse();

                jlab.addRow(jlab.addTr.pop(), true);
            }

            if(jlab.linkButton.length > 0) {
                jlab.linkButton.reverse();

                var $b = jlab.linkButton.pop();
                jlab.linkRow($b.closest("tr"), $b.attr("data-alarm-id"), true);
            }
        }
    }
});
$(document).on("click", "button.add", function() {
    jlab.addRow($(this).closest("tr"));
});
$(document).on("click", "button.remove", function() {
    jlab.removeRow($(this).closest("tr"));
});
$(document).on("click", "button.link", function() {
    jlab.linkRow($(this).closest("tr"), $(this).attr("data-alarm-id"));
});
$(document).on("click", "button.update", function() {
    jlab.updateRow($(this).closest("tr"));
});