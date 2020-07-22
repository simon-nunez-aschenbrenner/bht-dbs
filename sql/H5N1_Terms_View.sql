create view h5n1_terms(name) as
    (select * from
        (select h5n1_terms_a.name||' '||h5n1_terms_b.name
            from h5n1_terms_a, h5n1_terms_b) union
        (select h5n1_terms_b.name||' '||h5n1_terms_a.name
            from h5n1_terms_a, h5n1_terms_b) union
        (select h5n1_terms_c.name
            from h5n1_terms_c)
    );