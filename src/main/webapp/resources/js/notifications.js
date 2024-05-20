var jlab = jlab || {};
$(document).on("click", ".default-clear-panel", function () {
    $("#location-select").val(null).trigger('change');
    $("#priority-select").val('');
    $("#team-select").val('');
    $("#alarm-name").val('');
    $("#action-name").val('');
    $("#component-name").val('');
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
});