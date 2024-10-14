var jlab = jlab || {};

jlab.summaryRow = function($tr) {
    var id = $tr.attr("data-id"),
        $status = $tr.find(".status");

    $status
        .height($status.height())
        .width($status.width())
        .empty().append('<div class="button-indicator"></div>');

    var request = jQuery.ajax({
        url: "/jaws/setup/syncs/" + id + '?summary=Y',
        type: "GET",
        dataType: "json"
    });

    request.done(function(json) {
        if (json.error === undefined) {
            $status.replaceWith("Success!");

            var $tr = jlab.summaryTr.pop();
            if($tr !== undefined) {
                jlab.summaryRow($tr);
            }
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to obtain sync rule summary; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
    });

    request.always(function() {
        $status.empty().text("Pending");
    });
};

$(document).ready(function () {
    jlab.summaryTr = [];

    $(".rule-row").each(function () {
        jlab.summaryTr.push($(this));
    });

    if(jlab.summaryTr.length > 0) {
        jlab.summaryTr.reverse();

        jlab.summaryRow(jlab.summaryTr.pop());
    }
})