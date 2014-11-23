<?php
	$loggedIn = false;
	$userName = isset($_POST["name"]) ? $_POST["name"] : null;
	$userPass = isset($_POST["pass"]) ? $_POST["pass"] : null;
	$userPass = md5($userPass);

	if (isset($_SESSION['id'])) {
		$id = $_SESSION['id'];
		$query = "SELECT * FROM users WHERE id = '$id'";
		$result = mysqli_query($con, $query);

		$row = mysqli_fetch_array($result);

		if (!$row) {
			echo "<div>";
			echo "No existing user or wrong password.";
			echo "</div>";
		} else {
			$loggedIn = true;
			$_SESSION["id"] = $row['id'];
			$_SESSION["name"] = $row['name'];
			$_SESSION["superadmin"] = $row['superadmin'];
			$_SESSION["access_level"] = $row['level_of_access'];
			$_SESSION["institution_id"] = $row['institution_id'];
		}
	} else if ($userName && $userPass) {
		$query = "SELECT * FROM users WHERE email = '$userName' AND password = '$userPass'";
		$result = mysqli_query($con, $query);

		$row = mysqli_fetch_array($result);

		if (!$row) {
			echo "<div>";
			echo "No existing user or wrong password.";
			echo "</div>";
		} else {
			$loggedIn = true;
			$_SESSION["id"] = $row['id'];
			$_SESSION["email"] = $row['email'];
			$_SESSION["name"] = $row['name'];
			$_SESSION["surname"] = $row['surname'];
			$_SESSION["superadmin"] = $row['superadmin'];
			$_SESSION["access_level"] = $row['level_of_access'];
			$_SESSION["institution_id"] = $row['institution_id'];
			header("Location:" . "index.php");
		}
	}
	$userid = $_SESSION["id"];
	$email= $_SESSION["email"];
	$name= $_SESSION["name"];
	$surname= $_SESSION["surname"];
	$superadmin= $_SESSION["superadmin"];
	$access_level= $_SESSION["access_level"];
	$institution_id= $_SESSION["institution_id"];
?>