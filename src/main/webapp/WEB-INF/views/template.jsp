<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
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
            <t:slidemenu>
                <ol>
                    <li>Dashboard</li>
                    <li>Inventory</li>
                    <li>Help</li>
                </ol>
            </t:slidemenu>
            <nav aria-label="breadcrumb">
                <ol>
                    <li>Dashboard</li>
                </ol>
            </nav>
            <nav aria-label="secondary">
                <ol>
                    <li>Secondary 1</li>
                    <li>Secondary 2</li>
                </ol>
            </nav>
        </header>
        <script>
            let dialog = document.querySelector("dialog");
            dialog.addEventListener("click", e => {
                const dialogDimensions = dialog.getBoundingClientRect()
                if (
                    e.clientX < dialogDimensions.left ||
                    e.clientX > dialogDimensions.right ||
                    e.clientY < dialogDimensions.top ||
                    e.clientY > dialogDimensions.bottom
                ) {
                    dialog.close()
                }
            });
        </script>
    </body>
</html>