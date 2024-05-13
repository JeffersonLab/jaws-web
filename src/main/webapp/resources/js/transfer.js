var jlab = jlab || {};
jlab.addActions = function () {
    var actions = $("#actions-textarea").val(),
        reloading = false;

    $("#actions-import-button")
        .height($("#actions-import-button").height())
        .width($("#actions-import-button").width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: "/jaws/ajax/setup/add-action-key-value-list",
        type: "POST",
        data: {
            actions: actions
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
        window.console && console.log('Unable to add action key value list; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function () {
        if (!reloading) {
            $("#actions-import-button").empty().text("Import");
        }
    });
};
$(document).on("click", "#actions-import-button", function () {
    jlab.addActions();
});