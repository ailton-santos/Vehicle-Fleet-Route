<?php
    // Try to move the uploaded file
    $ret = move_uploaded_file($_FILES["uploadfile"]["tmp_name"], "/tmp/new.xlsx");

    // If $ret is not 0, we are successful
    if($ret)
    {
        echo json_encode(array("Success"));
    }
	else
    {
        echo json_encode(array("Failure"));
    }

	// Invoke the python script to geocode addresses and populate the database
	// Settings were to be changed in php.ini and apache2.conf to prevent timeout
    $command = escapeshellcmd('/var/www/FlaskApp1/FlaskApp1/populate1.py');
    shell_exec($command);
?>

