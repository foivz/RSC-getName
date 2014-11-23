<?php
	include 'config.php';
	include 'utils.php';
	$con = mysqli_connect($hostname, $db_username, $db_password, $db_name) or die("?");
	$sql = $_POST['sql'];
	if(strpos($sql, 'INSERT') !== false){
		if (mysqli_query($con, $sql)) {
			echo "success";
		} else {
			echo "fail". mysqli_error($con);
		}
	}else if(strpos($sql, 'UPDATE') !== false){
		if (mysqli_query($con, $sql)) {
			echo "success";
		} else {
			echo "fail". mysqli_error($con);
		}
	}else if(strpos($sql, 'extrainfo;') !== false){
		$sql = str_replace("extrainfo;","",$sql);
		$tmpmail = $sql;
		$sql="SELECT * FROM users WHERE email='$tmpmail'";
		$query = mysqli_query($con, $sql);
		$row = mysqli_fetch_assoc($query);
		$datname = $row['name'];
		$last = $row['last'];
		$tmpuid = $row['id'];
		$gender = $row['gender'];
		$sql="SELECT * FROM donation_info WHERE donor_id=$tmpuid";
		$result = mysqli_query($con, $sql);
		if ($result) {
			echo mysqli_num_rows($result);
		} else {
			echo "fail". mysqli_error($con);
		}
		$sql="SELECT * FROM donation_info WHERE donor_id=$tmpuid";
		$result = mysqli_query($con, $sql);
		$amt=0;
		while ($row = mysqli_fetch_assoc($result)) {
			$amt+=$row['amount'];
		}
		echo ";".$amt;
		echo ";".$datname;
		echo ";".$last;
		$unix_time_last = strtotime($last);
		$diff = time()-$unix_time_last;
		echo ";".(($gender=="M")?(3-floor(($diff/2592000))):(4-floor(($diff/2592000))));
	}else{
		$result = mysqli_query($con,$sql);
		$json = array();
		if(mysqli_num_rows($result)){
			while($row = mysqli_fetch_assoc($result)){
				$json['info'][]=$row;
			}
		}
		mysqli_close($con);
		$jsonencoded = json_encode($json);
		if($jsonencoded == "[]"){
			echo "no_results";
		}else{
			echo $jsonencoded;
		}
	}
	mysqli_close($con);
?> 