<?php
	$query = "SELECT * FROM institutions";

	$result = mysqli_query($con, $query);

	echo '<table class="table table-striped"><tr><td>ID</td><td>Ime</td><td>Opis</td><td>Promijeni podatke</td><td>Izbrisi instituciju</td></tr>';
	if (mysqli_num_rows($result) > 0) {
		while ($row = mysqli_fetch_assoc($result)) {
			echo "<tr>" . "<td>" . $row['id'] . '</td><td style>' . $row['name'] . "&nbsp;</td><td>" . $row['description'] . '&nbsp;</td><td><form action="' . "index.php" . '" method="post">
    	<button class="btn btn-success" type="submit" name="changeinfo" ><i class="fa fa-exchange"></i>Promijeni podatke</button>
    	<input type="hidden" name="id" value="' . $row['id'] . '" /></form></td><td><form onsubmit="return confirmFunc(this);" action="' . "index.php" . '" method="post">
    	<button class="btn btn-danger" type="submit" name="deleteinstitution"><i class="fa fa-trash"></i> Izbrisi instituciju</button>
    	<input type="hidden" name="id" value="' . $row['id'] . '" /></form></td></tr>';
		}
	}
	echo "</table>";
?>
<td style=""