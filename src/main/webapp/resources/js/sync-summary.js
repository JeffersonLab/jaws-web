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
            $status.parent().replaceWith('<td class="first-stat-td">' + json.matchCount + '</td><td class="stat-td">' + json.addCount + '</td><td class="stat-td">' + json.removeCount + '</td><td class="stat-td">' + json.updateCount + '</td><td class="stat-td">' + json.linkCount + '</td>');

            if(json.addCount > 0 || json.removeCount > 0 || json.updateCount > 0 || json.linkCount > 0) {
                $row.addClass('needs-attention');
            }

            jlab.matchCount = jlab.matchCount + json.matchCount;
            jlab.addCount = jlab.addCount + json.addCount;
            jlab.removeCount = jlab.removeCount + json.removeCount;
            jlab.updateCount = jlab.updateCount + json.updateCount;
            jlab.linkCount = jlab.linkCount + json.linkCount;

            jlab.$totalRow.find("th:nth-child(2)").text(jlab.integerWithCommas(jlab.matchCount));
            jlab.$totalRow.find("th:nth-child(3)").text(jlab.integerWithCommas(jlab.addCount));
            jlab.$totalRow.find("th:nth-child(4)").text(jlab.integerWithCommas(jlab.removeCount));
            jlab.$totalRow.find("th:nth-child(5)").text(jlab.integerWithCommas(jlab.updateCount));
            jlab.$totalRow.find("th:nth-child(6)").text(jlab.integerWithCommas(jlab.linkCount));

            var $tr = jlab.summaryTr.pop();
            if($tr !== undefined) {
                jlab.summaryRow($tr);
            } else {
                jlab.endTime = performance.now();
                $("#total-status-cell").empty().text("Done in " + ((jlab.endTime - jlab.startTime) / 1000).toFixed(1) + " seconds");
                $("#diff-button").empty().removeAttr("disabled").text("Diff");
            }
        } else {
            alert(json.error);
            $("#total-status-cell").empty().text("Error");
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to obtain sync rule summary; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
    });

    request.always(function() {
        $status.empty().text("Pending");
    });
};
jlab.diff = function() {

    let $button = $("#diff-button");

    $("#total-status-cell").empty();

    jlab.startTime = performance.now();

    $button
        .height($button.height())
        .width($button.width())
        .attr("disabled", "disabled")
        .empty().append('<div class="button-indicator"></div>');

    jlab.matchCount = 0;
    jlab.addCount = 0;
    jlab.removeCount = 0;
    jlab.updateCount = 0;
    jlab.linkCount = 0;

    jlab.$totalRow = $("#total-row");

    jlab.$totalRow.find("th:nth-child(2)").text(jlab.integerWithCommas(jlab.matchCount));
    jlab.$totalRow.find("th:nth-child(3)").text(jlab.integerWithCommas(jlab.addCount));
    jlab.$totalRow.find("th:nth-child(4)").text(jlab.integerWithCommas(jlab.removeCount));
    jlab.$totalRow.find("th:nth-child(5)").text(jlab.integerWithCommas(jlab.updateCount));
    jlab.$totalRow.find("th:nth-child(6)").text(jlab.integerWithCommas(jlab.linkCount));

    jlab.summaryTr = [];

    $(".stat-td").remove();

    $(".rule-row").each(function () {
        let $tr = $(this),
            $firstStatTd = $tr.find(".first-stat-td");

        $firstStatTd.replaceWith('<td class="first-stat-td" colspan="5"><div class="status">Pending</div></td>');
        jlab.summaryTr.push($tr);

        $tr.removeClass("needs-attention");
    });

    if(jlab.summaryTr.length > 0) {
        jlab.summaryTr.reverse();

        jlab.summaryRow(jlab.summaryTr.pop());
    }
};
$(document).on("click", "#diff-button", function () {
        jlab.diff();
});