var jlab = jlab || {};
jlab.acknowledge = function() {
    var reloading = false,
        nameArray = [],
        idArray = [];

    $("#notification-table .inner-table .selected-row").each(function () {
        var alarmName = $(this).closest("tr").find(":nth-child(1) a").text(),
            alarmId = $(this).closest("tr").attr("data-id");

        nameArray.push(alarmName);
        idArray.push(alarmId);
    });

    $("#acknowledge-button")
        .height($("#acknowledge-button").height())
        .width($("#acknowledge-button").width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: "/jaws/ajax/unset-override",
        type: "POST",
        data: {
            type: 'Latched',
            'name[]': nameArray
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
        window.console && console.log('Unable to acknowledge alarms; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to acknowledge: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $("#acknowledge-button").empty().text("Acknowledge");
        }
    });
};
jlab.unsuppress = function() {
    var reloading = false,
        nameArray = [],
        idArray = [],
        type = $('input[name="unsuppress-type"]:checked').val();

    $("#notification-table .inner-table .selected-row").each(function () {
        var alarmName = $(this).closest("tr").find(":nth-child(1) a").text(),
            alarmId = $(this).closest("tr").attr("data-id");

        nameArray.push(alarmName);
        idArray.push(alarmId);
    });

    $("#unsuppress-button")
        .height($("#unsuppress-button").height())
        .width($("#unsuppress-button").width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: "/jaws/ajax/unset-override",
        type: "POST",
        data: {
            'name[]': nameArray,
            type: type
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
        window.console && console.log('Unable to unsuppress alarms; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to unsuppress: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $("#unsuppress-button").empty().text("Save");
        }
    });
};
jlab.suppress = function () {
    var reloading = false,
        nameArray = [],
        idArray = [],
        type = $('input[name="suppress-type"]:checked').val();

    var comments = $("#suppress-comments").val(),
        oneshot = $("#oneshot").is(":checked"),
        expiration = $("#shelve-expiration").val(),
        reason = $("#shelve-reason").val();

    $("#notification-table .inner-table .selected-row").each(function () {
        var alarmName = $(this).closest("tr").find(":nth-child(1) a").text(),
            alarmId = $(this).closest("tr").attr("data-id");

        nameArray.push(alarmName);
        idArray.push(alarmId);
    });

    $("#suppress-button")
        .height($("#suppress-button").height())
        .width($("#suppress-button").width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: "/jaws/ajax/set-override",
        type: "POST",
        data: {
            'name[]': nameArray,
            type: type,
            comments: comments,
            oneshot: oneshot,
            expiration: expiration,
            reason: reason
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
        window.console && console.log('Unable to suppress alarms; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to suppress: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $("#suppress-button").empty().text("Save");
        }
    });
};
jlab.openSuppressDialog = function () {
    var nameArray = [],
        idArray = [],
        reasonArray = [];

    if ($("#notification-table .inner-table .selected-row").length < 1) {
        window.console && console.log('No rows selected');
        return;
    }

    $("#notification-table .inner-table .selected-row").each(function () {
        var alarmName = $(this).closest("tr").find(":nth-child(1) a").text(),
            alarmId = $(this).closest("tr").attr("data-id");

        nameArray.push(alarmName);
        idArray.push(alarmId);
    });

    var $selectedList = $("#selected-row-list");

    $selectedList.attr("data-id-json", JSON.stringify(idArray));

    $selectedList.empty();

    for (var i = 0; i < nameArray.length; i++) {
        $selectedList.append('<li>' + nameArray[i] + '</li>');
    }

    var count = $("#selected-count").text() * 1;
    var entityStr = (count === 1) ? ' Alarm' : ' Alarms';
    $("#suppress-dialog-alarm-count").text(count + entityStr);

    $("#type-disabled").prop("checked", true).trigger("click");
    $("#suppress-comments").val('');
    $("#oneshot").prop("checked", false);
    $("#shelve-reason")[0].selectedIndex = 0;
    $("#shelve-expiration").val('').trigger('change');
    $("#shelve-fieldset").prop('disabled', true);

    $("#suppress-dialog").dialog("open");
};
jlab.openUnsuppressDialog = function () {
    var nameArray = [],
        idArray = [];

    if ($("#notification-table .inner-table .selected-row").length < 1) {
        window.console && console.log('No rows selected');
        return;
    }

    $("#notification-table .inner-table .selected-row").each(function () {
        var alarmName = $(this).closest("tr").find(":nth-child(1) a").text(),
            alarmId = $(this).closest("tr").attr("data-id");

        nameArray.push(alarmName);
        idArray.push(alarmId);
    });

    var $selectedList = $("#unsuppress-selected-row-list");

    $selectedList.attr("data-id-json", JSON.stringify(idArray));

    $selectedList.empty();

    for (var i = 0; i < nameArray.length; i++) {
        $selectedList.append('<li>' + nameArray[i] + '</li>');
    }

    var count = $("#selected-count").text() * 1;
    var entityStr = (count === 1) ? ' Alarm' : ' Alarms';
    $("#unsuppress-dialog-alarm-count").text(count + entityStr);

    $("#type-reenable").prop("checked", true);

    $("#unsuppress-dialog").dialog("open");
};
$(document).on("change", "input[name=suppress-type]", function () {
    $("#shelve-fieldset").prop('disabled', true);
    if($(this).val() === "Shelved") {
        if($(this).is(":checked")) {
            $("#shelve-fieldset").prop('disabled', false);
        }
    }
});
$(document).on("click", "#suppress-button", function () {
    jlab.suppress();
});
$(document).on("click", "#unsuppress-button", function () {
    jlab.unsuppress();
});
$(document).on("click", "#acknowledge-button", function () {
    jlab.acknowledge();
});
$(document).on("click", "#open-suppress-button", function () {
    jlab.openSuppressDialog();
});
$(document).on("click", "#open-unsuppress-button", function () {
    jlab.openUnsuppressDialog();
});
$(document).on("click", ".default-clear-panel", function () {
    $("#state-select").val('');
    $("#override-select").val('');
    $("#type-select").val('');
    $("#location-select").val(null).trigger('change');
    $("#priority-select").val('');
    $("#team-select").val('');
    $("#alarm-name").val('');
    $("#action-name").val('');
    $("#component-name").val('');
    return false;
});
jlab.initDialog = function () {
    $(".dialog").dialog({
        autoOpen: false,
        width: 700,
        height: 700,
        modal: true,
        resizable: false
    });
};
function formatLocation(location) {
    return location.text.trim();
}
$(function () {
    jlab.initDialog();

    $("#location-select").select2({
        width: 390,
        templateSelection: formatLocation
    });

    /*Custom time picker*/
    var myControl = {
        create: function (tp_inst, obj, unit, val, min, max, step) {
            $('<input class="ui-timepicker-input" value="' + val + '" style="width:50%">')
                .appendTo(obj)
                .spinner({
                    min: min,
                    max: max,
                    step: step,
                    change: function (e, ui) { // key events
                        // don't call if api was used and not key press
                        if (e.originalEvent !== undefined)
                            tp_inst._onTimeChange();
                        tp_inst._onSelectHandler();
                    },
                    spin: function (e, ui) { // spin events
                        tp_inst.control.value(tp_inst, obj, unit, ui.value);
                        tp_inst._onTimeChange();
                        tp_inst._onSelectHandler();
                    }
                });
            return obj;
        },
        options: function (tp_inst, obj, unit, opts, val) {
            if (typeof (opts) === 'string' && val !== undefined)
                return obj.find('.ui-timepicker-input').spinner(opts, val);
            return obj.find('.ui-timepicker-input').spinner(opts);
        },
        value: function (tp_inst, obj, unit, val) {
            if (val !== undefined)
                return obj.find('.ui-timepicker-input').spinner('value', val);
            return obj.find('.ui-timepicker-input').spinner('value');
        }
    };

    $(".date-time-field").datetimepicker({
        dateFormat: 'dd-M-yy',
        controlType: myControl,
        timeFormat: 'HH:mm'
    }).mask("99-aaa-9999 99:99", {placeholder: " "});
});