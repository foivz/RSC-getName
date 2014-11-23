<?
$apiKey = "AIzaSyBB2US-xN51FbcTQggfF_X812RJck5oREo";
$devices = array('APA91bHXygX3Hxc-k-P426Du_93udkCGKYngf1JY_iP-UhJg-jPWjMi234Ywuwi2kW6BURv7tgDP-PqfeSiTv-NyrmJrm858SaZGS68b3jqIqtK7C9ZNROlN1tJ_M1Z1KhY4YcDltdH4LqOn40AzXfg3Qkq348y6iw');
$message = "The message to send";

$gcpm = new GCMPushMessage($apiKey);
$gcpm->setDevices($devices);
$response = $gcpm->send($message, array('title' => 'Test title'));
?>