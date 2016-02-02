CREATE TABLE department_account
(
  dept_account_num NUMBER(6),
  date_established DATE,
  account_det NUMBER(8,2),
  PRIMARY KEY(dept_account_num),
  CHECK(date_established > DATE '2015-10-31')
);

CREATE TABLE process_account
(
  proc_account_num NUMBER(6),
  date_established DATE,
  account_det NUMBER(8,2),
  PRIMARY KEY(proc_account_num),
  CHECK(date_established > DATE '2015-10-31')
);

CREATE TABLE assem_account
(
  assem_account_num NUMBER(6),
  date_established DATE,
  account_det NUMBER(8,2),
  PRIMARY KEY(assem_account_num),
  CHECK(date_established > DATE '2015-10-31')
);

CREATE TABLE customer
(
  name VARCHAR2(20),
  address VARCHAR2(50) NOT NULL,
  PRIMARY KEY(name)
);

CREATE TABLE assembly
(
  assembly_id CHAR(8),
  date_ordered DATE,
  assem_account_num NUMBER(6),
  assembly_details VARCHAR2(20),
  cust_name VARCHAR2(20),
  PRIMARY KEY(assembly_id),
  FOREIGN KEY(assem_account_num) REFERENCES assem_account(assem_account_num),
  FOREIGN KEY(cust_name) REFERENCES customer(name),
  CHECK(date_ordered > DATE '1990-01-01'),
  CHECK(assembly_id LIKE 'as\d{6}')
);

CREATE TABLE department
(
  dept_num NUMBER(2),
  dept_data VARCHAR2(20),
  dept_account_num NUMBER(6),
  PRIMARY KEY(dept_num),
  FOREIGN KEY(dept_account_num) REFERENCES department_account(dept_account_num)
);

CREATE TABLE fit_job
(
  fit_job_num NUMBER(6),
  start_date DATE NOT NULL,
  end_date DATE,
  labor_time NUMBER(3),
  PRIMARY KEY(fit_job_num),
  CHECK(start_date < end_date OR end_date IS NULL),
  CHECK(labor_time >= 0)
);

CREATE INDEX fit_end_index ON fit_job(end_date);

CREATE TABLE paint_job
(
  paint_job_num NUMBER(6),
  start_date DATE NOT NULL,
  end_date DATE,
  color CHAR(3),
  volume NUMBER(4,2),
  labor_time NUMBER(3),
  PRIMARY KEY(paint_job_num),
  CHECK(start_date < end_date OR end_date IS NULL),
  CHECK(labor_time >= 0 AND volume >= 0)
);

CREATE INDEX paint_end_index ON paint_job(end_date);

CREATE TABLE cut_job
(
  cut_job_num NUMBER(6),
  start_date DATE NOT NULL,
  end_date DATE,
  mach_type CHAR(3),
  mach_use NUMBER(3),
  labor_time NUMBER(3),
  PRIMARY KEY(cut_job_num),
  CHECK(start_date < end_date OR end_date IS NULL),
  CHECK(labor_time >= 0)
);

CREATE INDEX cut_end_index ON cut_job(end_date);

CREATE TABLE transaction
(
  transaction_num NUMBER(6),
  sup_cost NUMBER(8,2),
  fit_job_num NUMBER(6),
  pnt_job_num NUMBER(6),
  cut_job_num NUMBER(6),
  PRIMARY KEY(transaction_num),
  FOREIGN KEY(fit_job_num) REFERENCES fit_job(fit_job_num),
  FOREIGN KEY(pnt_job_num) REFERENCES paint_job(paint_job_num),
  FOREIGN KEY(cut_job_num) REFERENCES cut_job(cut_job_num),
  CHECK(sup_cost >= 0),
  CHECK(transaction_num >= 100000),
  CHECK
  (
    (fit_job_num IS NULL AND pnt_job_num IS NULL) 
    OR (cut_job_num IS NULL AND pnt_job_num IS NULL) 
    OR (fit_job_num IS NULL AND cut_job_num IS NULL)
  )
);

CREATE TABLE fit_process
(
  fit_process_id CHAR(8),
  process_data VARCHAR2(20),
  fit_type VARCHAR2(10) NOT NULL,
  department_num NUMBER(2),
  fit_account_num NUMBER(6),
  PRIMARY KEY(fit_process_id),
  FOREIGN KEY(department_num) REFERENCES department(dept_num),
  FOREIGN KEY(fit_account_num) REFERENCES process_account(proc_account_num),
  CHECK(fit_process_id LIKE 'fp\d{6}')
);

CREATE TABLE paint_process
(
  paint_process_id CHAR(8),
  process_data VARCHAR2(20),
  paint_type VARCHAR2(10) NOT NULL,
  paint_method VARCHAR2(10) NOT NULL,
  department_num NUMBER(2),
  paint_account_num NUMBER(6),
  PRIMARY KEY(paint_process_id),
  FOREIGN KEY(department_num) REFERENCES department(dept_num),
  FOREIGN KEY(paint_account_num) REFERENCES process_account(proc_account_num),
  CHECK(paint_process_id LIKE 'pp\d{6}')
);

CREATE INDEX paint_method_index ON paint_process(paint_method);

CREATE TABLE cut_process
(
  cut_process_id CHAR(8),
  process_data VARCHAR2(20),
  cut_type VARCHAR2(10) NOT NULL,
  machine_type VARCHAR2(10) NOT NULL,
  department_num NUMBER(2),
  cut_account_num NUMBER(6),
  PRIMARY KEY(cut_process_id),
  FOREIGN KEY(department_num) REFERENCES department(dept_num),
  FOREIGN KEY(cut_account_num) REFERENCES process_account(proc_account_num),
  CHECK(cut_process_id LIKE 'cp\d{6}')
);

CREATE TABLE undergo_fit
(
  fit_assembly_id CHAR(8),
  fit_process_id CHAR(8),
  fit_job_num NUMBER(6),
  PRIMARY KEY(fit_assembly_id, fit_process_id),
  FOREIGN KEY(fit_assembly_id) REFERENCES assembly(assembly_id),
  FOREIGN KEY(fit_process_id) REFERENCES fit_process(fit_process_id),
  FOREIGN KEY(fit_job_num) REFERENCES fit_job(fit_job_num)
);

CREATE INDEX fit_job_index ON undergo_fit(fit_job_num);

CREATE TABLE undergo_paint
(
  paint_assembly_id CHAR(8),
  paint_process_id CHAR(8),
  paint_job_num NUMBER(6),
  PRIMARY KEY(paint_assembly_id, paint_process_id),
  FOREIGN KEY(paint_assembly_id) REFERENCES assembly(assembly_id),
  FOREIGN KEY(paint_process_id) REFERENCES paint_process(paint_process_id),
  FOREIGN KEY(paint_job_num) REFERENCES paint_job(paint_job_num)
);

CREATE INDEX paint_job_index ON undergo_paint(paint_job_num);

CREATE TABLE undergo_cut
(
  cut_assembly_id CHAR(8),
  cut_process_id CHAR(8),
  cut_job_num NUMBER(6),
  PRIMARY KEY(cut_assembly_id, cut_process_id),
  FOREIGN KEY(cut_assembly_id) REFERENCES assembly(assembly_id),
  FOREIGN KEY(cut_process_id) REFERENCES cut_process(cut_process_id),
  FOREIGN KEY(cut_job_num) REFERENCES cut_job(cut_job_num)
);

CREATE INDEX cut_job_index ON undergo_cut(cut_job_num);

