<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8" data-context-path="${pageContext.request.contextPath}" data-app-version="${initParam['releaseNumber']}"/>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta name="description" content="Template">
        <title>Template</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/template.css"/>
    </head>
    <body>
        <header>
            <input type="checkbox" id="open-primary-nav">
            <label for="open-primary-nav" class="primary-nav-toggle">
                <div class="spinner diagonal part-1"></div>
                <div class="spinner horizontal"></div>
                <div class="spinner diagonal part-2"></div>
            </label>
            <nav class="primary-nav" aria-label="primary">
                <ol>
                    <li>Primary 1</li>
                    <li>Primary 2</li>
                </ol>
            </nav>
            <nav aria-label="breadcrumb">
                <ol>
                    <li>Crumb 1</li>
                    <li>Crumb 2</li>
                </ol>
            </nav>
            <nav aria-label="secondary">
                <ol>
                    <li>Secondary 1</li>
                    <li>Secondary 2</li>
                </ol>
            </nav>
        </header>
    </body>
</html>