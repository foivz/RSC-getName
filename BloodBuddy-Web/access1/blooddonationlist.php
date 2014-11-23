<?php
	$query = "SELECT * FROM donation_info WHERE institution_id='$institution_id'";

	$result = mysqli_query($con, $query);

	echo '<table class="table table-striped" ><tr><td>ID</td><td>Ime</td><td>Kolicina</td><td>Datum</td><td>Izbrisi instituciju</td></tr>';
	if (mysqli_num_rows($result) > 0) {
		while ($row = mysqli_fetch_assoc($result)) {
			$tmpuid = $row['donor_id'];
			$sql2="SELECT * FROM users WHERE id=$tmpuid";
			$query2 = mysqli_query($con2, $sql2);
			$row2 = mysqli_fetch_assoc($query2);
			echo "<tr>" . "<td>" . $row['id'] . "</td><td>" . $row2['name'] . "</td><td>" . $row['amount'] ."</td><td>" . $row['date'] . '&nbsp;</td><td><form onsubmit="return confirmChoiceFunc(\'Hocete li izbrisati donaciju?\');" action="' . "index.php" . '" method="post">
    	<button type="submit" name="deletedonation" class="btn btn-danger" >Izbrisi donaciju</button>
    	<input type="hidden" name="id" value="' . $row['id'] . '" /></form></td></tr>';
		}
	}
	echo "</table>";
?>