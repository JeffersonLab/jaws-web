var jlab = jlab || {};
jlab.editableRowTable = jlab.editableRowTable || {};
jlab.editableRowTable.entity = 'Component';
jlab.editableRowTable.dialog.width = 400;
jlab.editableRowTable.dialog.height = 300;
jlab.addRow = function() {
    var name = $("#row-name").val(),
        teamId = $("#row-team").val(),
        reloading = false;

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: "/jaws/ajax/add-component",
        type: "POST",
        data: {
            name: name,
            teamId: teamId
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
        window.console && console.log('Unable to add system; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
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
$(document).on("click", ".default-clear-panel", function () {
    $("#team-select").val('');
    $("#component-name").val('');
    return false;
});
$(document).on("table-row-add", function() {
    jlab.addRow();
});
$(document).on("table-row-edit", function() {
    jlab.editRow();
});