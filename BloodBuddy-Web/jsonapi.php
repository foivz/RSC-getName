<?php
	include 'config.php';
	$con = mysqli_connect($hostname, "json", "", $db_name) or die(mysql_error($con));
	$sql = $_POST['sql'];
	$result = mysqli_query($con,$sql);
	$json = array();
	if(mysqli_num_rows($result)){
		while($row = mysqli_fetch_assoc($result)){
			$json['info'][]=$row;
		}
	}
	mysqli_close($con);
	$jsonencoded = json_encode($json);
	echo $jsonencoded;
	mysqli_close($con);
?> 