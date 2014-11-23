<?php
	$query = "SELECT * FROM users WHERE institution_id='$institution_id' AND level_of_access=1";

	$result = mysqli_query($con, $query);

	echo '<table class="table table-striped" ><tr><td>ID</td><td>Ime</td><td>Prezime</td><td>Broj obavljenih donacija</td><td>Izbrisi korisnika</td></tr>';
	if (mysqli_num_rows($result) > 0) {
		while ($row = mysqli_fetch_assoc($result)) {
			$tmpuid = $row['id'];
			$sql2="SELECT * FROM donation_info WHERE blood_taken_by=$tmpuid";
			$query2 = mysqli_query($con2, $sql2);
			$num = mysqli_num_rows($query2);
			echo "<tr>" . "<td>" . $row['id'] . "</td><td>" . $row['name'] . "</td><td>" . $row['surname'] ."</td><td>" . $num . '&nbsp;</td><td><form onsubmit="return confirmChoiceFunc(\'Hocete li izbrisati korisnika?\');" action="' . "index.php" . '" method="post">
    	<button type="submit" name="deletedonation" class="btn btn-danger" >Izbrisi korisnika</button>
    	<input type="hidden" name="id" value="' . $row['id'] . '" /></form></td></tr>';
		}
	}
	echo "</table>";
?>