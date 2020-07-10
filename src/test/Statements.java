package test;

public class Statements {
	
	public static final String FRAGE_1_B =
			"select to_char(querydata.querytime, 'mm.dd'), count(distinct querydata.querytime) from aoldata.querydata where query like '%bird flu%' group by to_char(querydata.querytime, 'mm.dd') order by 1";

	public Statements() {
		// TODO Auto-generated constructor stub
	}

}
