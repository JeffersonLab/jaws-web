var jlab = jlab || {};

jlab.summaryRow = function($tr) {
    var id = $tr.attr("data-id"),
        $status = $tr.find(".status"),
        $row = $tr;

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
            $status.parent().replaceWith('<td>' + json.matchCount + '</td><td>' + json.addCount + '</td><td>' + json.removeCount + '</td><td>' + json.updateCount + '</td><td>' + json.linkCount + '</td>');

            if(json.addCount > 0 || json.removeCount > 0 || json.updateCount > 0 || json.linkCount > 0) {
                $row.addClass('needs-attention');
            }

            jlab.matchCount = jlab.matchCount + json.matchCount;
            jlab.addCount = jlab.addCount + json.addCount;
            jlab.removeCount = jlab.removeCount + json.removeCount;
            jlab.updateCount = jlab.updateCount + json.updateCount;
            jlab.linkCount = jlab.linkCount + json.linkCount;

            jlab.$totalRow.find("th:nth-child(2)").text(jlab.matchCount);
            jlab.$totalRow.find("th:nth-child(3)").text(jlab.addCount);
            jlab.$totalRow.find("th:nth-child(4)").text(jlab.removeCount);
            jlab.$totalRow.find("th:nth-child(5)").text(jlab.updateCount);
            jlab.$totalRow.find("th:nth-child(6)").text(jlab.linkCount);

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
    jlab.matchCount = 0;
    jlab.addCount = 0;
    jlab.removeCount = 0;
    jlab.updateCount = 0;
    jlab.linkCount = 0;

    jlab.$totalRow = $("#total-row");

    jlab.summaryTr = [];

    $(".rule-row").each(function () {
        jlab.summaryTr.push($(this));
    });

    if(jlab.summaryTr.length > 0) {
        jlab.summaryTr.reverse();

        jlab.summaryRow(jlab.summaryTr.pop());
    }
})