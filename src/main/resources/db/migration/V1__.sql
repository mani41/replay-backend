CREATE TABLE card_details
(
    card_number      VARCHAR(255) NOT NULL,
    cvv              INT          NOT NULL,
    card_holder_name VARCHAR(255) NULL,
    expiry           datetime     NULL,
    bank_name        VARCHAR(255) NULL,
    CONSTRAINT pk_card_details PRIMARY KEY (card_number)
);

CREATE TABLE cc_payments
(
    payment_id   BINARY(16)   NOT NULL,
    amount       DOUBLE       NOT NULL,
    payment_date datetime     NULL,
    payment_type SMALLINT     NULL,
    card_number  VARCHAR(255) NULL,
    CONSTRAINT pk_cc_payments PRIMARY KEY (payment_id)
);