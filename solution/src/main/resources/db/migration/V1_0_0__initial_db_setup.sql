/* Create expense table */
CREATE TABLE t_expense (
  id SERIAL PRIMARY KEY,
  date DATE NOT NULL,
  amount NUMERIC NOT NULL,
  reason TEXT NOT NULL
);

COMMENT ON TABLE t_expense IS 'Table containing expense records';
COMMENT ON COLUMN t_expense.id IS 'Expense ID';
COMMENT ON COLUMN t_expense.date IS 'The date on which expense was created';
COMMENT ON COLUMN t_expense.amount IS 'Expense amount with VAT included';
COMMENT ON COLUMN t_expense.reason IS 'Expense-related details and explanation';

/* Insert some initial records */
INSERT INTO t_expense (date, amount, reason)
VALUES
  ('2018-02-09', 10.2, 'Gasoline 50L'),
  ('2018-02-04', 20.2, 'Business lunch'),
  ('2018-02-01', 10120, 'Louis Vuitton HARLEM ANKLE Boots');
