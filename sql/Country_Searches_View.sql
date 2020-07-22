create view country_searches(search) as
    (select * from
        (select h5n1_terms.name||' '||countries.name
            from h5n1_terms, countries) union
        (select countries.name||' '||h5n1_terms.name
            from countries, h5n1_terms)
    );