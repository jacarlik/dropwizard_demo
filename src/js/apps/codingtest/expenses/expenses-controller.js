"use strict";

/******************************************************************************************

Expenses controller

******************************************************************************************/

var app = angular.module("expenses.controller", []);

app.controller("ctrlExpenses", ["$rootScope", "$scope", "config", "restalchemy", function ExpensesCtrl($rootScope, $scope, $config, $restalchemy) {
    // Update the headings
    $rootScope.mainTitle = "Expenses";
    $rootScope.mainHeading = "Expenses";

    // Update the tab sections
    $rootScope.selectTabSection("expenses", 0);

    // Expected date pattern ("dd-MM-yy")
    const datePattern = /^(\d{2})\/(\d{2})\/(\d{2})$/i;
    // Pattern for amount with supplied currency
    const amountPattern = /^(\d+(\.\d+)?)\s*?([A-z]{3})$/i;
    // Base currency
    const baseCurrency = "GBP";
    // Expenses endpoint
    var restExpenses = $restalchemy.init({ root: $config.apiroot, headers: $config.requestHeaders }).at("expenses");
    // Fixer.io endpoint
    var forexApi = $restalchemy.init({ root: $config.forexApi });

    $scope.dateOptions = {
        changeMonth: true,
        changeYear: true,
        dateFormat: "dd/mm/yy"
    };

    $scope.errors = null;

    /**
     * Handle expenses endpoint errors (non-200 responses)
     *
     * @param response Response object
     * @param statusCode HTTP status code
     */
    var handleSaveExpenseError = function(response, statusCode) {
        switch(statusCode) {
            // Validation errors
            case 422:
                $scope.errors = response.errors || [response.message];
                break;
            // Everything else (JSON parse exceptions, unrecoverable errors such as DB being inaccessible, etc.)
            default:
                $scope.errors = ["" +
                    "Unable to process the request. " +
                    "Please check if the input parameters are correct and contact your " +
                    "customer care agent if the problem still persists."
                ];
        }
    };

    /**
     * Handle fixer.io errors (non-200 responses)
     */
    var handleGetCurrencyRateError = function() {
        $scope.errors = ["Unable to retrieve currency rates for the API"];
    };

    /**
     * Add leading 0s to the given value
     *
     * @param value Given value to pad with 0s
     * @param width Total width of the padded value
     * @returns {*} Padded value
     */
    var pad = function (value, width) {
        value = value + '';
        return value.length >= width ? value : new Array(width - value.length + 1).join('0') + value;
    };

    /**
     * Retrieve expenses from the endpoint
     */
    var loadExpenses = function() {
        // Retrieve a list of expenses via REST using hardcoded offset/limits
        // TODO: Implement real pagination
        restExpenses.get({ offset: 0, limit: 500})
            .then(function(response) {
                $scope.errors = [];
                $scope.expenses = response;
            })
            .error(handleSaveExpenseError.bind(this));
    };

    /**
     * Calculate standard UK VAT (20%)
     *
     * @param amount Total amount including VAT
     * @returns {string} VAT portion
     */
    var calculateVat = function(amount) {
        var vat = parseFloat(amount - (amount / 1.2)).toFixed(2);
        if (isNaN(vat)) {
            return "N/A";
        } else {
            return vat;
        }
    };

    /**
     * Converts "dd/MM/yy" notation into "yyyy-MM-dd"
     *
     * @param dateString Original date string in "dd/MM/yy" format
     * @returns {string} Date string in "yyyy-MM-dd" format
     */
    var getFixerDateFormat = function (dateString) {
        var dateParts = dateString.split('/');
        // Expenses can't originate from the 20th century
        var baseDate = new Date(20 + dateParts[2], dateParts[1] - 1, dateParts[0]);
        return baseDate.getFullYear() + "-" + pad(baseDate.getMonth() + 1, 2) + "-" + pad(baseDate.getDate(), 2);
    };

    /**
     * Extract VAT from the given amount
     *
     * Note: If currency has been supplied along with the amount (i.e. 10 USD), convert the amount to GBP
     *       and extract the VAT
     */
    $scope.extractVat = function () {
        if ($scope.newExpense.amount) {
            var amountMatch = $scope.newExpense.amount.match(amountPattern);
            var dateMatch = $scope.newExpense.date.match(datePattern);
            if (amountMatch != null && dateMatch != null && amountMatch[3].toUpperCase() !== baseCurrency) {
                // Amount was specified in a different currency, proceed with the conversion
                forexApi.at(getFixerDateFormat($scope.newExpense.date)).get({base: amountMatch[3].toUpperCase(), symbols: baseCurrency})
                    .then(function (response) {
                        $scope.errors = [];
                        $scope.newExpense.vat = calculateVat(amountMatch[1] * response.rates.GBP);
                    })
                    .error(function () {
                        // i.e. invalid currency
                        handleGetCurrencyRateError();
                    });
            } else {
                var amount;
                if (amountMatch != null && amountMatch[3].toUpperCase() != null) {
                    amount = amountMatch[1];
                } else {
                    amount = $scope.newExpense.amount;
                }
                // Extract VAT from the total amount
                $scope.newExpense.vat = calculateVat(amount);
            }
        }
    };

    /**
     * Save new expense
     */
    $scope.saveExpense = function () {
        if ($scope.expensesform.$valid) {
            var request = {
                amount: $scope.newExpense.amount,
                country: $scope.newExpense.country,
                date: $scope.newExpense.date,
                reason: $scope.newExpense.reason
            };
            var match = request.amount.match(amountPattern);
            // Amount was specified in a different currency
            if (match != null) {
                request['currency'] = match[3];
                request['amount'] = match[1];
            }
            // Post the new expense
            restExpenses.post(request)
                .then(function () {
                    loadExpenses();
                })
                .error(handleSaveExpenseError.bind(this));
        }
    };

    $scope.clearExpense = function() {
        $scope.errors = [];
        $scope.newExpense = {};
    };

    // Initialise scope variables
    loadExpenses();
    $scope.clearExpense();
}]);
