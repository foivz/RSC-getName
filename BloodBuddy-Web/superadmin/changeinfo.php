<?php if (isset($_POST["changeinfo"])) {
	$instid = $_POST['id'];
	$query = "SELECT * FROM institutions WHERE id = '$instid'";

	$result = mysqli_query($con, $query);

	$row = mysqli_fetch_array($result);
	?>
	<form action="<?php echo "/index.php"; ?>" method="post">
		Ime
		<br/>
		<input type="text" name="name" value="<? echo $row['name'] ?>"/>
		<br/>
		Opis
		<br/>
		<textarea name="desc"><? echo $row['description'] ?></textarea>
		<br/>
		<input type="hidden" name="id" value="<? echo $instid; ?>"/>
		<button type="submit" name="changeinfo" class="btn btn-info" >Promjeni</button>
	</form>
<?php
}
?>