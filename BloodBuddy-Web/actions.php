<?php
if (isset($_POST["changeinfo"]) && isset($_POST["desc"])) {
    $instid = $_POST['id'];
    $desc = $_POST["desc"];
    $name = $_POST["name"];
    $sql = "UPDATE institutions SET name='$name', description='$desc' WHERE id='$instid'";
    mysqli_query($con, $sql);
    header("Location:" . "index.php");
}
if (isset($_POST['pocetna'])) {
    header("Location:" . "index.php");
}
if (isset($_POST["logout"])) {
    unset($_SESSION);
    session_destroy();
    header("Location:" . "index.php");
    die("Logged out");
}
if (isset($_POST["deleteinstitution"]) && $superadmin==1) {
            $instid = $_POST['id'];
            $sql = "DELETE FROM institutions WHERE id='$instid'";
            mysqli_query($con, $sql) or die(mysqli_error($con));
            header("Location:" . "index.php");
}
if (isset($_POST["deletedonation"]) && ($access_level == 4 || $access_level == 1)) {
            $donid = $_POST['id'];
            $sql = "DELETE FROM donation_info WHERE id='$donid'";
            mysqli_query($con, $sql) or die(mysqli_error($con));
            header("Location:" . "index.php");
}
?>