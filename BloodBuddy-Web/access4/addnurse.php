<?php
	if (isset($_POST["addnurse"]) || (isset($_GET['err']) && $_GET['loc'] == "addnurse")) {
		if (isset($_POST["addnurse"])) {
			if (isset($_POST["pwd2"]) && !isset($_GET['err'])) {
				$err = false;
				$errors = "";
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
				$sql = "SELECT * FROM users WHERE email='$email'";
				if ($result = mysqli_query($con, $sql)) {
					$rowcount = mysqli_num_rows($result);
					if ($rowcount > 0) {
						$errors .= "<br>Duplicate%20entry!";
						$err = true;
					}
					mysqli_free_result($result);
				}
				if ($err == true) {
					header("Location: " . "index.php" . "?err=true&loc=addnurse&errors=" . $errors);
				}
				$pwd = md5($pwd);
				$query = "SELECT id FROM institutions WHERE main_user = '$id'";
				$result = mysqli_query($con, $query);
				$row = mysqli_fetch_array($result);
				$institutionid = $row['id'];
				$sql = "INSERT INTO users (email,password,name,surname,institution_id,level_of_access)
VALUES ('$email','$pwd','$name','$surname','$institutionid',1)";
				if (mysqli_query($con, $sql)) {
					echo "<br>New nurse created successfully!";
				} else {
					echo "<br>Error: " . $sql . "<br>" . mysqli_error($conn);
				}
			} else {
				?>
				<form role="form" method="post" action="<? echo "index.php"; ?>">
					<br/>
					Email adresa:
					<br/>
					<input type="email" name="email" required="required" class="form-control"/>
					<br/>
					Ime:
					<br/>
					<input type="text" name="name" required="required" class="form-control"/>
					<br/>
					Prezime:
					<br/>
					<input type="text" name="surname" required="required" class="form-control"/>
					<br/>
					Sifra:
					<br/>
					<input type="password" name="pwd" required="required" class="form-control"/>
					<br/>
					Ponovi sifru:
					<br/>
					<input type="password" name="pwd2" required="required" class="form-control"/>
					<br/>
					<button type="submit" name="addnurse" class="btn btn-success">Dodaj</button>
				</form>
			<?php
			}
		}
	} else {
		?>
		<form action=" <? echo "index.php"; ?>" method="post">
			<button type="submit" name="addnurse" class="btn btn-info" >Dodaj medicinskog tehnicara/sestru</button>
		</form>
	<?php
	}
?>