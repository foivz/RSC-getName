<html>
<head>
	<link href="//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
	<link rel="stylesheet" href="http://bootswatch.com/lumen/bootstrap.css">
    <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon">
	<link rel="icon" href="/favicon.ico" type="image/x-icon">
	<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
	<script>
		function confirmFunc(form) {
			return confirm('Hocete li stvarno izbrisati ovu instituciju?');
		}
		function confirmDonationFunc(form) {
			return confirm('Jeste li sigurni da su svi podatci tocni?');
		}
		function confirmChoiceFunc(text) {
			return confirm(text);
		}
		function showResult(str) {
			if (str.length == 0) {
				document.getElementById("livesearch").innerHTML = "";
				document.getElementById("livesearch").style.border = "0px";
				return;
			}
			if (window.XMLHttpRequest) {
				// code for IE7+, Firefox, Chrome, Opera, Safari
				xmlhttp = new XMLHttpRequest();
			} else {  // code for IE6, IE5
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			}
			xmlhttp.onreadystatechange = function () {
				if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
					document.getElementById("livesearch").innerHTML = xmlhttp.responseText;
					document.getElementById("livesearch").style.border = "1px solid #A5ACB2";
				}
			}
			xmlhttp.open("GET", "livesearch.php?search=" + str, true);
			xmlhttp.send();
		}
		function addToBox(variable, var2) {
			document.getElementById("livesearchbox").value = var2;
			document.getElementById("bloodid").value = variable;
		}
	</script>
    <title>Blood Buddy</title>
</head>
<body>
<?php
	error_reporting(E_ALL);
	session_start();
	include 'mysqlconnect.php';

	include 'auth.php';

	include 'actions.php';

	if (!$loggedIn) {
		include 'login.php';
	} else {
		include 'header.php';
		if ($superadmin == 1) {
			include 'superadmin/addinstitution.php';
			include 'superadmin/changeinfo.php';
			include 'superadmin/institutiontable.php';
		} else {
			if ($_SESSION["access_level"] == 1) {
				?>
				<form action="index.php" method="post" role="form" class="form-inline">
					<input type="text" id="livesearchbox" name="hhh" onKeyUp="showResult(this.value)" class="form-control" required="true" autocomplete="off"/><input class="form-control" id="bloodid" name="bloodid" style="width:8ch" readonly="readonly" required="true"/>

					<div id="livesearch"></div>
					<input class="form-control" type="number" max="500" id="bloodamt" name="bloodamt" style="width:8ch" required="true"/>
					<button type="submit" name="adddonation" class="btn btn-info" >Dodaj donaciju</button>
				</form>
				<?
				if (isset($_POST['adddonation'])) {
					$tmpuid = $_POST['bloodid'];
					$sql2 = "SELECT * FROM users WHERE id=$tmpuid";
					$query2 = mysqli_query($con2, $sql2);
					$row2 = mysqli_fetch_assoc($query2);
					$amt = $_POST['bloodamt'];
					$blood_type = $row2['blood_type'];
					$rh = $row2['rh'];
					$date = date('Y-m-d H:i:s');
					$sql = "INSERT INTO donation_info (amount,donor_id,blood_taken_by,institution_id,blood_type,rh_factor,date) VALUES ($amt,$tmpuid,$userid,$institution_id,'$blood_type','$rh', '$date')";
					if (mysqli_query($con, $sql)) {
						mysqli_query($con, "UPDATE users SET last='$date' WHERE id=$tmpuid");
					} else {
						echo "Error: " . $sql . "<br>" . mysqli_error($conn);
					}
				}
				include 'access1/adddonation.php';
				include 'access1/blooddonationlist.php';
			} else if ($_SESSION["access_level"] == 4) {
				$id = $_SESSION['id'];
				$query = "SELECT * FROM institutions WHERE main_user='$id'";
				$result = mysqli_query($con, $query);
				$row = mysqli_fetch_assoc($result);
				echo "<br/>Vi ste upravitelj institucije " . $row["name"];
				include 'access4/changeinfo.php';
				include 'access4/addnurse.php';
				include 'access4/blooddonationlist.php';
				include 'access4/nurselist.php';
			}
		}
	}
?>
</body>
</html>