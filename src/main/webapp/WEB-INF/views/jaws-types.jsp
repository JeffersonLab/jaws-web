<%@page contentType="application/javascript" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
const jawsTypes = {};
<t:type-map id="alarms" model="${model.alarmModel}"/>
<t:type-map id="registrations" model="${model.registrationModel}"/>
<t:type-map id="activations" model="${model.activationModel}"/>
<t:type-map id="overrides" model="${model.overrideModel}"/>
<t:type-map id="classes" model="${model.classModel}"/>
<t:type-map id="instances" model="${model.instanceModel}"/>
<t:type-map id="notifications" model="${model.notificationModel}"/>
<t:type-map id="locations" model="${model.locationModel}"/>
<t:type-map id="categories" model="${model.categoryModel}"/>
export default jawsTypes;