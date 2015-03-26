using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using TCX.Configuration;

namespace OMSamples.Samples
{
    [SampleCode("dn_monitor")]
    [SampleDescription("Output CallUpdate events for all connected external DNs")]
    class DNmonitorSample : ISample
    {
        public void Run(params string[] args)
        {
                foreach (DN dn in PhoneSystem.Root.GetDN())
                {		    
                    ActiveConnection[] a = dn.GetActiveConnections();
                    if (a.Length > 0)
                    {
                        foreach (ActiveConnection ac in a)
                        {
			    if (!(ac.DN is ExternalLine) || (ac.Status != ConnectionStatus.Connected)) {
			      continue;
			    }
			    System.Console.WriteLine("<CallUpdate xmlns=\"http://pbxevents.jjinterna.com/model\">" +
                            "<callId>" + ac.CallID + "</callId>" +
                            "<callDuration>" +  (int)(DateTime.UtcNow - ac.LastChangeStatus).TotalSeconds + "</callDuration>" +
                            "<callingNumber>" + ac.InternalParty.Number + "</callingNumber>" +
                            "<calledNumber>" + ac.DialedNumber + "</calledNumber>" +
		                        "</CallUpdate>");
                        }
                    }
                }
        }
    }
}
