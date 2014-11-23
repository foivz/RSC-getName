<?php
include 'config.php';
$con = mysqli_connect($hostname, $db_username, $db_password, $db_name);
if (!$con) {
    echo "<div>";
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    echo "</div>";
    die();
}
$con2 = mysqli_connect($hostname, $db_username, $db_password, $db_name);
if (!$con2) {
    echo "<div>";
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    echo "</div>";
    die();
}
?>