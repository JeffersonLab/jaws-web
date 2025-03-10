var jlab = jlab || {};
jlab.sanitizeConfig = {
    ALLOWED_TAGS: ['p', '#text', 'h1', 'h2', 'h3', 'em', 'strong', 'ul', 'ol', 'li', 'a', 'table', 'thead', 'tbody', 'tr', 'td', 'th'],
    KEEP_CONTENT: false
};

jlab.initMarkdownWidgets = function() {
    $(".markdown-widget").each(function (item) {
        let markdown = $(this).find(".markdown-text").text(),
            rendered = DOMPurify.sanitize(marked.parse(markdown), jlab.sanitizeConfig);

        $(this).find(".markdown-html").html(rendered);
    });
};
jlab.doneLoading = function() {
    jlab.initMarkdownWidgets();
};

$(function () {
    jlab.initMarkdownWidgets();
});
