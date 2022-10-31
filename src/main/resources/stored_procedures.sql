DELIMITER //
CREATE PROCEDURE get_by_department(IN dept_name varchar(50))
BEGIN
	SELECT * FROM employees WHERE department = dept_name;
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE salary_higher(IN salary_amount double)
BEGIN
  SELECT * FROM employees WHERE salary > salary_amount;
END //
DELIMITER ;