/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/security/PermissionItem.java $ 
 * $Id: PermissionItem.java 55478 2008-12-01 19:21:44Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008 Etudes, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 * Portions completed before July 1, 2004 Copyright (c) 2003, 2004 Rafael Steil, All rights reserved, licensed under the BSD license. 
 * http://www.opensource.org/licenses/bsd-license.php 
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met: 
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer. 
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution. 
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE 
 ***********************************************************************************/
package org.etudes.jforum.security;

import java.util.List;

/**
 * Guarda os itens e os dados de um determinado campo de permissao.
 * No formulario de edicao das permissoes de acesso de algum grupo ou usuario,
 * esta classe encapsula o nome, id e os itens do campo de permissao.
 * 
 * @author Rafael Steil
 */
public class PermissionItem 
{
	/**
	 * Nome da permissao
	 * */
	private String name;
	
	/**
	 * ID da permissao
	 * */
	private String id;
	
	/**
	 * Tipo ( Campo se multipla escolha ou unica )
	 * */
	private String type;
	
	/**
	 * Itens do campo ( id=valor)
	 * */
	private List data;
	
	public static final int SINGLE = 1;
	public static final int MULTIPLE = 2;	
	
	/**
	 * Cria um novo objeto.
	 * 
	 * @param name Nome da permissao
	 * @param id ID da permissao
	 * @param type Tipo da permissao. <code>SINGLE</code> ou <code>MULTIPLE</code>
	 * @param data ArrayList com itens do campo ( contendo objetos do tipo <code>FormSelectedData</code>
	 * */
	public PermissionItem(String name, String id, String type, List data)
	{
		this.name = name;
		this.id = id;
		this.type = type;
		this.data = data;
	}
	
	/**
	 * Pega o nome da permissao.
	 * 
	 * @reutrn String contendo o nome da permissao
	 * */
	public String getName()
	{
		return this.name;
	}
	
	/**
	 * Pega o ID da permisao.
	 * 
	 * @return String contendo o ID da permissao
	 * */
	public String getId()
	{
		return this.id;
	}
	
	/**
	 * Pega o tipo de permissao.
	 * 
	 * @return int contendo o tipo. Para campo de unica escolha,
	 * retorna o valor de <code>SINGLE</code>, e <code>MULTIPLE</code>
	 * se o campo for de multipla escolha
	 * */
	public String getType()
	{
		return this.type;
	}
	
	/**
	 * Pega os itens do campo.
	 * 
	 * @param ArrayList contendo os itens da permissao. Cada posicao
	 * do ArrayList eh um objeto do tipo <code>FormSelectedData</code>
	 * */
	public List getData()
	{
		return this.data;
	}
}