package org.dbfit.greenpepper;

import org.dbfit.core.DBEnvironment;
import org.dbfit.core.DbStatement;
import org.dbfit.core.DbStoredProcedure;
import org.dbfit.core.DbTable;
import org.dbfit.greenpepper.fixture.QueryFixture;
import org.dbfit.greenpepper.util.GreenPepperTestHost;
import org.dbfit.mysql.MySqlEnvironment;

import com.greenpepper.GreenPepper;
import com.greenpepper.document.Document;
import com.greenpepper.reflect.Fixture;
import com.greenpepper.reflect.PlainOldFixture;
import com.greenpepper.systemunderdevelopment.DefaultSystemUnderDevelopment;

import dbfit.util.Log;
import dbfit.util.Options;

public class DatabaseSystemUnderDevelopment extends DefaultSystemUnderDevelopment {
	protected DBEnvironment environment;
	public DatabaseSystemUnderDevelopment() {
		super.addImport("org.dbfit.greenpepper");
        GreenPepper.addImport( "org.dbfit.greenpepper");
	}
	public void onEndDocument(Document document) {
		if (Options.isDebugLog()) Log.log("end document");
		try {
			if (Options.isDebugLog()) Log.log("Rolling back");
			environment.closeConnection();

		} catch (Exception e) {
			throw new Error(e);
		}
		super.onEndDocument(document);
	}

	public void onStartDocument(Document document) {
		if (Options.isDebugLog()) Log.log("start document");		
		GreenPepperTestHost.getInstance().clearSymbols();
		super.onStartDocument(document);
	}
	public Fixture getFixture(String name, String... params) throws Throwable {
		name=name.toUpperCase().trim();
		if (name.equals("MYSQL")){
			this.environment=new MySqlEnvironment();
			return new PlainOldFixture(new ConnectionProperties(environment, params));
		}			
		if (name.equals("TABLE")){
			return new PlainOldFixture(new DbTable(environment, params[0]));
		}			
		if (name.equals("STATEMENT")){
			return new PlainOldFixture(new DbStatement(environment, params[0],GreenPepperTestHost.getInstance()));
		}			
		if (name.equals("QUERY")){
			return new QueryFixture(environment,params[0]);
		}
		if (name.equals("PROCEDURE")||name.equals("FUNCTION")){
			return new PlainOldFixture(new DbStoredProcedure(environment, params[0]));
		}
		return super.getFixture(name, params);
	}	
}
