/* Create expense table */
CREATE TABLE t_expense (
  id SERIAL PRIMARY KEY,
  date DATE NOT NULL,
  amount NUMERIC NOT NULL,
  reason TEXT NOT NULL
);

COMMENT ON TABLE t_expense IS 'Contains expenses';
COMMENT ON COLUMN t_expense.id IS 'Expense identifier';
COMMENT ON COLUMN t_expense.date IS 'Expense date';
COMMENT ON COLUMN t_expense.amount IS 'Expense amount including VAT';
COMMENT ON COLUMN t_expense.reason IS 'Expense-related details and explanation';

/* Insert some initial values */
INSERT INTO t_expense (date, amount, reason)
VALUES
  ('2018-02-09', 10.2, 'Gasoline 50L'),
  ('2018-02-04', 20.2, 'Business lunch'),
  ('2018-02-01', 10120, 'Louis Vuitton HARLEM ANKLE Boots');
