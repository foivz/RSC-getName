<?php
include 'config.php';
$con = mysqli_connect($hostname, $db_username, $db_password, $db_name);
if (!$con) {
    echo "<div>";
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
    echo "</div>";
    die();
}
$search = $_GET['search'];
$sql="SELECT * FROM users WHERE name LIKE '$search%' OR surname LIKE '$search%'";
$result = mysqli_query($con,$sql);
$returnstring = "";
while($row = mysqli_fetch_array($result)) {
	$unix_time_last = strtotime($row['last']);
	$diff = time()-$unix_time_last;
	if(($row['gender']=="M" && ($diff/2592000)>3)||($row['gender']=="F" && ($diff/2592000)>4)){
  $returnstring .= '<a onClick="addToBox('.$row['id'].', \''.$row['name'] . " " . $row['surname'] .'\')">'.$row['name'] . " " . $row['surname'] . "</a><br>";
	}else{
  $returnstring .= '<a>'.$row['name'] . " " . $row['surname'] . ", Još " . (($row['gender']=="M")?(3-floor(($diff/2592000))):(4-floor(($diff/2592000)))) . " mjeseca</a><br>";
	}
}
function str_lreplace($search, $replace, $subject)
{
    $pos = strrpos($subject, $search);

    if($pos !== false)
    {
        $subject = substr_replace($subject, $replace, $pos, strlen($search));
    }

    return $subject;
}
echo str_lreplace("<br>", "", $returnstring);
mysqli_close($con);
?>