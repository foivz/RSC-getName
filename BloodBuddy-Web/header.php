<form action=" <? echo "index.php"; ?>" method="post" style="text-align:center">
        <button type="submit" name="logout" class="btn btn-warning" >Odjava</button>
        <button type="submit" name="home" class="btn btn-primary" >Pocetna</button>
    </form>
<?php
    $userName = $_SESSION["name"];
    echo "Ulogirani ste kao $userName!";
    if (isset($_GET['err'])) {
        echo $_GET['errors'];
    }
?>