<%@tag description="Slide Menu Widget" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<button class="metal-button" onclick="document.querySelector('dialog').showModal();" type="button">☰</button>
<input type="checkbox" id="open-primary-nav">
<label for="open-primary-nav" class="primary-nav-toggle">
    <div class="spinner diagonal part-1"></div>
    <div class="spinner horizontal"></div>
    <div class="spinner diagonal part-2"></div>
</label>
<dialog class="primary-nav">
<nav aria-label="primary">
    <jsp:doBody/>
</nav>
</dialog>