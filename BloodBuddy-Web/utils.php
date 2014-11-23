<?php
	function getNumOfDonations($_userid, $con){
		$sql="SELECT * FROM donation_info WHERE donor_id=$_userid";
		$result = mysqli_query($con, $sql);
		if ($result) {
			return mysqli_num_rows($result);
		} else {
			return "fail". mysqli_error($con);
		}	
	}
	function totalBloodDonated($_userid, $con){
		$sql="SELECT * FROM donation_info WHERE donor_id=$_userid";
		$result = mysqli_query($con, $sql);
		$amt=0;
		while ($row = mysqli_fetch_assoc($result)) {
			$amt+=$row['amount'];
		}
		return $amt;
	}
	function mailToID($_email, $con){
		$sql="SELECT * FROM users WHERE email='$_email'";
		echo $sql;
		$query = mysqli_query($con, $sql);
		if($query){}else{echo mysqli_error($con);}
		$row = mysqli_fetch_assoc($query);
		echo mysqli_error($con);
		echo $row['id'];	
	}
?>