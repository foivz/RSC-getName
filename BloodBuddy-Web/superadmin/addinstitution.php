<?php
	if (isset($_POST['addinstitution']) &&(!isset($_POST['inst']) || isset($_GET['err']))) {
		?>
		<form role="form" method="post" action="<? echo "index.php"; ?>">
						<br/>
			Ime institucije:
			<br/>
			<input type="text" name="inst" required="required" class="form-control"/>
			<br/>
			Email adresa glavnog korisnika:
			<br/>
			<input type="email" name="email" required="required" class="form-control"/>
			<br/>
			Ime glavnog korisnika:
			<br/>
			<input type="text" name="name" required="required" class="form-control"/>
			<br/>
			Prezime glavnog korisnika:
			<br/>
			<input type="text" name="surname" required="required" class="form-control"/>
			<br/>
			Sifra glavnog korisnika:
			<br/>
			<input type="password" name="pwd" required="required" class="form-control"/>
			<br/>
			Ponovi sifru glavnog korisnika:
			<br/>
			<input type="password" name="pwd2" required="required" class="form-control"/>

            <button type="submit" name="addinstitution" class="btn btn-success" >Registriraj instituciju</button>
		</form>
	<?php
	} else if (!isset($_POST['addinstitution'])) {
		?>
		<form action=" <? echo "index.php"; ?>" method="post">

			<button type="submit" name="addinstitution" class="btn btn-info" >Dodaj instituciju</button>
		</form>
	<?php
	} else if (isset($_POST['addinstitution']) && isset($_POST['inst']) && !isset($_GET['err'])){
		$err = false;
		$errors = "";
		$inst = $_POST['inst'];
		$email = $_POST['email'];
		$name = $_POST['name'];
		$surname = $_POST['surname'];
		$pwd = $_POST['pwd'];
		$pwd2 = $_POST['pwd2'];
		unset($_SESSION['ERR']);
		if ($pwd != $pwd2) {
			$errors .= "<br>Passwords%20do%20not%20match!";
			$err = true;
		}
		$sql = "SELECT * FROM users WHERE email='$email' OR name ='$inst'";
		if ($result = mysqli_query($con, $sql)) {
			$rowcount = mysqli_num_rows($result);
			if ($rowcount > 0) {
				$errors .= "<br>Duplicate%20entry!";
				$err = true;
			}
			mysqli_free_result($result);
		}
		if ($err == true) {
			header("Location: " . "index.php" . "?err=true&loc=addinst&errors=" . $errors);
		}
		$pwd = md5($pwd);
		$sql = "INSERT INTO institutions (name) VALUES ('$inst')";
		if (mysqli_query($con, $sql)) {
			echo "New institution created successfully!";
		} else {
			echo "Error: " . $sql . "<br>" . mysqli_error($conn);
		}
		$query = "SELECT id FROM institutions WHERE name = '$inst'";
		$result = mysqli_query($con, $query);
		$row = mysqli_fetch_array($result);
		$institutionid = $row['id'];
		$sql = "INSERT INTO users (email,password,name,surname,institution_id,level_of_access)
VALUES ('$email','$pwd','$name','$surname','$institutionid',4)";
		if (mysqli_query($con, $sql)) {
			echo "<br>New admin user created successfully!";
		} else {
			echo "<br>Error: " . $sql . "<br>" . mysqli_error($conn);
		}
		$query = "SELECT id FROM users WHERE email = '$email'";
		$result = mysqli_query($con, $query);
		$row = mysqli_fetch_array($result);
		$uid = $row['id'];
		$sql = "UPDATE institutions SET main_user='$uid' WHERE id='$institutionid'";
		if (mysqli_query($con, $sql)) {
			echo "<br>New admin user set successfully!";
		} else {
			echo "<br>Error: " . $sql . "<br>" . mysqli_error($conn);
		}
	}

?>