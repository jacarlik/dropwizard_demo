/* Simple table containing VAT rates per country
 *
 * Note: This is a simple representation of VAT rates; more realistic approach would include having breakdown of VATs per type (standard, reduced and whatnot)
 */
CREATE TABLE t_standard_vat_rate (
  id SERIAL PRIMARY KEY,
  country CHAR(3) UNIQUE NOT NULL,
  rate NUMERIC NOT NULL
);

COMMENT ON TABLE t_standard_vat_rate IS 'Contains standard VAT rates for each country';
COMMENT ON COLUMN t_standard_vat_rate.id IS 'VAT country/rate identifier';
COMMENT ON COLUMN t_standard_vat_rate.country IS 'ISO Alpha-3 country code';
COMMENT ON COLUMN t_standard_vat_rate.rate IS 'Standard VAT rate';

/* Insert a couple of VAT rates */
INSERT INTO t_standard_vat_rate (country, rate)
VALUES ('GBR', 0.2), ('GGY', 0), ('IMN', 0.2);

/* Create expense table */
CREATE TABLE t_expense (
  id SERIAL PRIMARY KEY,
  date DATE NOT NULL,
  amount NUMERIC NOT NULL,
  reason TEXT NOT NULL,
  country CHAR(3) REFERENCES t_standard_vat_rate (country) /* Referencing country for simplicity purposes */
);

COMMENT ON TABLE t_standard_vat_rate IS 'Contains expenses which include the VAT tax';
COMMENT ON COLUMN t_expense.id IS 'Expense identifier';
COMMENT ON COLUMN t_expense.date IS 'Expense date';
COMMENT ON COLUMN t_expense.amount IS 'Expense amount including VAT';
COMMENT ON COLUMN t_expense.reason IS 'Expense-related details and explanation';
COMMENT ON COLUMN t_expense.country IS 'Identifies the country and its corresponding standard VAT rate';

/* Insert some initial values */
INSERT INTO t_expense (date, amount, reason, country)
VALUES
  ('2018-02-09', 10.2, 'Gasoline 50L', 'GBR'),
  ('2018-02-04', 20.2, 'Business lunch', 'GGY'),
  ('2018-02-01', 10120, 'Louis Vuitton HARLEM ANKLE Boots', 'IMN');
