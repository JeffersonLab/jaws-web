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

// START CODE THAT OVERRIDES SMOOTHNESS
jlab.pageDialog.modal = false;
jlab.showPartialPageDialog = function($dialog, title) {
    $dialog.dialog({
        title: title,
        autoOpen: true,
        modal: jlab.pageDialog.modal,
        width: jlab.pageDialog.width,
        height: jlab.pageDialog.height,
        minWidth: jlab.pageDialog.minWidth,
        minHeight: jlab.pageDialog.minHeight,
        resizable: jlab.pageDialog.resizable,
        close: function () {
            $(this).dialog('destroy').remove();
        }
    });
    $(document).trigger('partial-page-init');
};
jlab.openPageInDialog = function (href) {
    try {
        let url = new URL(href, window.location.href);
        url.searchParams.set('partial', 'Y');

        let $dialog = $("<div class=\"page-dialog\"></div>");

        fetch(url, {redirect: "error"})
            .then((res) => {
                if (!res.ok) {
                    throw new Error(`HTTP error! Status: ${res.status}`);
                }
                return res.text();
            })
            .then(function (html) {
                const parser = new DOMParser();
                return parser.parseFromString(html, "text/html");
            })
            .then(data => {
                let $data = $(data),
                    html = $data.find("#partial-html"),
                    css = $data.find("#partial-css"),
                    js = $data.find("#partial-js");

                $dialog.html(html);

                let title = $data.find("#partial").attr("data-title");

                $('link[rel="stylesheet"]', css).each(function () {
                    let href = $(this).attr("href");

                    if($('link[rel="stylesheet"][href="' + href + '"]').length === 0) {
                        $(document).find("head").append(this);
                    }
                });

                let waitingForLoadCount = 0;
                let scripts = [];

                $('script', js).each(function () {
                    let src = $(this).attr("src");

                    // We ignore script elements missing src attribute
                    if(src && $('script[src="' + src + '"]').length === 0) {
                        waitingForLoadCount++;

                        const script = document.createElement('script');
                        scripts.push(script);
                        script.onload = function(){
                            waitingForLoadCount--;

                            if(waitingForLoadCount === 0) {
                                jlab.showPartialPageDialog($dialog, title);
                            }
                        };
                        script.src = src;
                    }
                });

                if(waitingForLoadCount === 0) {
                    jlab.showPartialPageDialog($dialog, title);
                } else {
                    // Start load
                    for (let i = 0; i < scripts.length; i++) {
                        let script = scripts[i];
                        document.body.appendChild(script);
                        //$(document).find("body").append(this); // jQuery will block load
                    }
                }
            }).catch(error => {
            console.error('fetch error:', error);
            // assume auth redirect
            let loginHref = $("#login-link").attr("href");
            if(loginHref) {
                window.location.href = loginHref;
            }
        });
    } catch (e) {
        console.log('URL Error: ', href, e);
    }
};
