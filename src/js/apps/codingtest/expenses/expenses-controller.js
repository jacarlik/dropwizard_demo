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

    const amountPattern = /^(\d+(\.\d+)?)\s*?([A-z]{3})$/i;
    var restExpenses = $restalchemy.init({ root: $config.apiroot, headers: $config.requestHeaders }).at("expenses");

    $scope.dateOptions = {
        changeMonth: true,
        changeYear: true,
        dateFormat: "dd/mm/yy"
    };

    $scope.errors = null;

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

    var calculateVat = function(amount) {
        return parseFloat(amount - (amount / 1.2)).toFixed(2);
    };

    $scope.extractVat = function () {
        if ($scope.newExpense.amount) {
            var match = $scope.newExpense.amount.match(amountPattern);
            if (match == null) {
                // Extract VAT from the total amount
                $scope.newExpense.vat = calculateVat($scope.newExpense.amount);
            }
        }
    };

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
